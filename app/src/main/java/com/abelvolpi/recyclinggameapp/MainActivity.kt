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
                // Return true or false of DragEvent.getResult().
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Determine whether this View can accept the dragged data.
                    if (dragEvent.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        // Invalidate the view to force a redraw in the new tint.
                        view.invalidate()

                        // Return true to indicate that the View can accept the dragged
                        // data.
                        true
                    } else {
                        // Return false to indicate that, during the current drag and
                        // drop operation, this View doesn't receive events again until
                        // ACTION_DRAG_ENDED is sent.
                        false
                    }
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    itemFocused(view)
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> true

                DragEvent.ACTION_DRAG_EXITED -> {
                    itemUnfocused(view)
                    true
                }

                DragEvent.ACTION_DROP -> {
                    itemUnfocused(view)
                    // Get the item containing the dragged data.
                    val item: ClipData.Item = dragEvent.clipData.getItemAt(0)

                    // Get the text data from the item.
                    val dragData = item.text

                    val id = view.tag
                    Toast.makeText(this, "id: $id // itemType: $dragData", Toast.LENGTH_LONG).show()

                    setupNewRandomTrashItem()


                    // Turn off color tints.
                    (view as? ImageView)?.clearColorFilter()

                    // Invalidate the view to force a redraw.
                    view.invalidate()


                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    trashItem.visibility = View.VISIBLE
                    true
                }

                else -> {
                    false
                }
            }
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
