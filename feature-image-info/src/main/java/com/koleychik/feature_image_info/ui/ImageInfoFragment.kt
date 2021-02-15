package com.koleychik.feature_image_info.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.koleychik.basic_resources.Constants
import com.koleychik.feature_image_info.R
import com.koleychik.feature_image_info.databinding.FragmentImageInfoBinding
import com.koleychik.feature_image_info.di.ImageInfoFeatureComponentHolder
import com.koleychik.feature_image_info.transformDateToDateFormat
import com.koleychik.feature_image_info.ui.adapter.ImageInfoAdapter
import com.koleychik.feature_image_info.ui.viewModels.ImageInfoViewModel
import com.koleychik.feature_image_info.ui.viewModels.ViewModelFactory
import com.koleychik.models.fileCarcass.media.ImageModel
import javax.inject.Inject

class ImageInfoFragment : Fragment() {

    private var _binding: FragmentImageInfoBinding? = null
    private val binding get() = _binding!!

    @Inject
    internal lateinit var adapter: ImageInfoAdapter

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[ImageInfoViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageInfoBinding.inflate(layoutInflater, container, false)
        ImageInfoFeatureComponentHolder.getComponent().inject(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        createSetOnClickListener()
        subscribe()
    }

    private fun subscribe() {
        viewModel.isBarOpen.observe(viewLifecycleOwner, {
            if (it) openBars()
            else closeBars()
        })
        viewModel.currentImagePosition.observe(viewLifecycleOwner, { updateUI(adapter.list[it]) })
    }

    private fun shareImage(model: ImageModel) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, model.uri)
            type = "image/*"
        }
        startActivity(intent)
    }

    private fun updateUI(model: ImageModel) {
        with(binding) {
            title.text = model.name
            dateCreated.text = transformDateToDateFormat(model.dateAdded)
        }
    }

    private fun closeBars() {
        binding.motionLayout.transitionToState(R.id.closeBar)
    }

    private fun openBars() {
        binding.motionLayout.transitionToState(R.id.openBars)
    }

    private fun setupViewPager() {
        with(binding) {
            viewPager.adapter = adapter
            viewPager.registerOnPageChangeCallback(createRegisterOnPageChangeCallback())
            viewPager.currentItem = getStarPosition()
        }
        adapter.submitList(getListImages())
    }

    private fun createRegisterOnPageChangeCallback() = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            Toast.makeText(requireContext(), "position = $position", Toast.LENGTH_SHORT).show()
            viewModel.currentImagePosition.value = position
        }
    }

    private fun getListImages(): List<ImageModel> =
        requireArguments().getParcelableArrayList(Constants.PARCELABLE_LIST) ?: emptyList()

    private fun getStarPosition() = viewModel.currentImagePosition.value
        ?: requireArguments().getInt(Constants.PARCELABLE_POSITION, 0)

    private fun createSetOnClickListener() {

        val onClickListener = View.OnClickListener {
            when (it.id) {
                R.id.trashCan -> viewModel.deleteImage(adapter.list[viewModel.currentImagePosition.value!!])
                R.id.viewPager -> viewModel.isBarOpen.value = viewModel.isBarOpen.value ?: true
                R.id.share -> shareImage(adapter.list[viewModel.currentImagePosition.value!!])
            }
        }

        with(binding) {
            trashCan.setOnClickListener(onClickListener)
            share.setOnClickListener(onClickListener)
            viewPager.setOnClickListener(onClickListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}