package com.abelvolpi.recyclinggameapp

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val trashItem: ImageView by lazy { findViewById(R.id.trash_item) }
    private val recyclePlastic: ImageView by lazy { findViewById(R.id.recycle_plastic) }
    private val recyclePaper: ImageView by lazy { findViewById(R.id.recycle_paper) }
    private val recycleMetal: ImageView by lazy { findViewById(R.id.recycle_metal) }
    private val recycleGlass: ImageView by lazy { findViewById(R.id.recycle_glass) }
    private val recycleOrganic: ImageView by lazy { findViewById(R.id.recycle_organic) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpDragAndDropView()
        setUpDragAndDropListeners()
        setupNewRandomTrashItem()
    }

    private fun setUpDragAndDropView() {
        trashItem.setOnLongClickListener {
            val item = ClipData.Item(it.tag as CharSequence)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(it.tag.toString(), mimeTypes, item)

            val dragShadow = DragShadowBuilder(it)

            it.startDragAndDrop(data, dragShadow, it, 0)
            it.visibility = View.INVISIBLE

            true
        }
    }

    private fun setUpDragAndDropListeners() {
        val listener = View.OnDragListener { view, dragEvent ->

            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_ENTERED -> {
                    itemFocused(view)
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    itemUnfocused(view)
                }

                DragEvent.ACTION_DROP -> {
                    itemUnfocused(view)
                    // Get the item containing the dragged data.
                    val item: ClipData.Item = dragEvent.clipData.getItemAt(0)

                    // Get the text data from the item.
                    val dragData = item.text.toString()

                    val id = view.tag.toString()

                    checkCorrectRecyclingBin(id, dragData)

                    setupNewRandomTrashItem()

                    (view as? ImageView)?.clearColorFilter()

                    view.invalidate()
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    trashItem.visibility = View.VISIBLE
                }
            }
            true
        }
        recyclePlastic.setOnDragListener(listener)
        recyclePaper.setOnDragListener(listener)
        recycleMetal.setOnDragListener(listener)
        recycleGlass.setOnDragListener(listener)
        recycleOrganic.setOnDragListener(listener)
    }

    private fun itemFocused(view: View) {
        (view as? ImageView)?.alpha = 0.5f
        val anim = AnimationUtils.loadAnimation(this, R.anim.scale_in)
        view.startAnimation(anim)
        anim.fillAfter = true
    }

    private fun itemUnfocused(view: View) {
        (view as? ImageView)?.alpha = 1.0f
        val anim = AnimationUtils.loadAnimation(this, R.anim.scale_out)
        view.startAnimation(anim)
        anim.fillAfter = true
    }

    private fun setupNewRandomTrashItem() {
        val trashName = Trash.values().toList().shuffled().first().name
        val trashImageResource = setUpTrashImageView(trashName)

        trashItem.tag = trashName
        trashItem.setImageResource(trashImageResource)
    }

    private fun setUpTrashImageView(trashName: String): Int {
        return when (trashName) {
            Trash.PLASTIC.name -> R.drawable.gallon
            Trash.PAPER.name -> R.drawable.paper
            Trash.METAL.name -> R.drawable.metal
            Trash.GLASS.name -> R.drawable.glass
            Trash.ORGANIC.name -> R.drawable.apple
            else -> -1
        }
    }
}
