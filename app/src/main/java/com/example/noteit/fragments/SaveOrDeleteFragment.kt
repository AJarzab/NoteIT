package com.example.noteit.fragments

import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.noteit.R
import com.example.noteit.activity.MainActivity
import com.example.noteit.databinding.BottomSheetLayoutBinding
import com.example.noteit.databinding.FragmentSaveOrDeleteBinding
import com.example.noteit.model.Note
import com.example.noteit.utils.hideKeyboard
import com.example.noteit.viewModel.NoteActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class SaveOrDeleteFragment : Fragment(R.layout.fragment_save_or_delete) {
   private lateinit var navController: NavController
   private lateinit var contentBinding: FragmentSaveOrDeleteBinding
   private var note: Note?=null
    private var color=-1
    private lateinit var result: String
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private val currentDate= SimpleDateFormat.getInstance().format(Date())
    private val job= CoroutineScope(Dispatchers.Main)
    private val args: SaveOrDeleteFragmentArgs by navArgs()
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val animation= MaterialContainerTransform().apply {
            drawingViewId=R.id.fragment
            scrimColor= Color.TRANSPARENT
            duration= 300L
        }
        sharedElementEnterTransition=animation
        sharedElementReturnTransition=animation

    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentBinding= FragmentSaveOrDeleteBinding.bind(view)

        navController= Navigation.findNavController(view)
        val activity= activity as MainActivity

        contentBinding.backBtn.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }

        ViewCompat.setTransitionName(
            contentBinding.noteContentFragmentParent,
            "recyclerView_${args.note?.id}"
        )

        contentBinding.saveNote.setOnClickListener{
            saveNote()
        }

        try {
            contentBinding.etNoteContent.setOnFocusChangeListener{_, hasFocus->
                if (hasFocus){
                    contentBinding.bottomBar.visibility=View.VISIBLE
                    contentBinding.etNoteContent.setStylesBar(contentBinding.styleBar)
                } else contentBinding.bottomBar.visibility=View.GONE
                }
            } catch (e: Throwable){
                Log.d("TAG", e.stackTraceToString())
        }

        val pickPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val source = ImageDecoder.createSource(requireActivity().contentResolver, it)
                val bitmap = ImageDecoder.decodeBitmap(source)

                val imageView = contentBinding.noteImage
                imageView.setImageBitmap(bitmap)

                imageUrl = it.toString()
//                contentBinding.deletePhotoButton.visibility = View.VISIBLE
            }
        }

        contentBinding.fabAddPhoto.setOnClickListener{
            pickPhoto.launch("image/*")
        }

        contentBinding.deletePhotoButton.setOnClickListener{
            contentBinding.noteImage.setImageBitmap(null)
            contentBinding.deletePhotoButton.visibility = View.GONE
            imageUrl = null
        }

        contentBinding.fabColorPick.setOnClickListener{
            val bottomSheetDialog=BottomSheetDialog(
                requireContext(),
                R.style.BottomSheetDialogTheme
            )
            val bottonSheetView: View=layoutInflater.inflate(
                R.layout.bottom_sheet_layout, null
            )
            with(bottomSheetDialog){
                setContentView(bottonSheetView)
                show()
            }

            val bottomSheetBinding=BottomSheetLayoutBinding.bind(bottonSheetView)
            bottomSheetBinding.apply {
                colorPicker.apply {
                    setSelectedColor(color)
                    setOnColorSelectedListener {
                        value ->
                        color=value
                        contentBinding.apply {
                            noteContentFragmentParent.setBackgroundColor(color)
                            toolbarFragmentNoteContent.setBackgroundColor(color)
                            bottomBar.setBackgroundColor(color)
                            activity.window.statusBarColor=color
                        }
                        bottomSheetBinding.bottomSheetParent.setCardBackgroundColor(color)
                    }
                }
                bottomSheetParent.setBackgroundColor(color)
            }

            bottonSheetView.post{
                bottomSheetDialog.behavior.state=BottomSheetBehavior.STATE_EXPANDED
            }
        }

        //open with existing note
        setUpNote()
    }

    private fun setUpNote() {
        val note=args.note
        val title=contentBinding.etTitle
        val content=contentBinding.etNoteContent
        val lastEdited=contentBinding.lastEdited

        if (note==null){
            contentBinding.lastEdited.text= getString(R.string.edited_on, SimpleDateFormat.getDateInstance().format(Date()))
        }
        if (note!= null){
            title.setText(note.title)
            content.renderMD(note.content)
            lastEdited.text=getString(R.string.edited_on,note.date)
            color=note.color
            contentBinding.apply {
                job.launch {
                    delay(10)
                    noteContentFragmentParent.setBackgroundColor(color)
                }
                toolbarFragmentNoteContent.setBackgroundColor(color)
                bottomBar.setBackgroundColor(color)
            }
            activity?.window?.statusBarColor=note.color

            note.imageUrl?.let {
                Glide.with(this)
                    .load(it)
                    .into(contentBinding.noteImage)
            }
        }
        note?.let {
            if (it.imageUrl != null) {
                contentBinding.deletePhotoButton.visibility = View.VISIBLE
            } else {
                contentBinding.deletePhotoButton.visibility = View.GONE
            }
        }

    }

    private fun saveNote() {
        if(contentBinding.etNoteContent.text.toString().isEmpty() || contentBinding.etTitle.text.toString().isEmpty()){
            Toast.makeText(activity, "Something is Empty", Toast.LENGTH_SHORT).show()
        }
        else {
            note=args.note
            when(note) {
                null -> {
                    noteActivityViewModel.saveNote(
                        Note(
                            0,
                            contentBinding.etTitle.text.toString(),
                            contentBinding.etNoteContent.getMD(),
                            currentDate,
                            currentDate,
                            color,
                            imageUrl
                        )
                    )

                    result = "Note Saved"
                    setFragmentResult(
                        "key",
                        bundleOf("bundleKey" to result)
                    )

                    navController.navigate(SaveOrDeleteFragmentDirections.actionSaveOrDeleteFragmentToNoteFragment())
                }
                else ->
                {
                    //update note
                    updateNote()
                    navController.popBackStack()
                }
        }
    }}

    private fun updateNote() {
        if (note!=null)
        {
            noteActivityViewModel.updateNote(
                Note(
                    note!!.id,
                    contentBinding.etTitle.text.toString(),
                    contentBinding.etNoteContent.getMD(),
                    currentDate,
                    note!!.date1,
                    color,
                    imageUrl
                )
            )
        }
    }

}