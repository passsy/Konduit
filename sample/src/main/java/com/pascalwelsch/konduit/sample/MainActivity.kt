/*
 * Copyright (C) 2018 Pascal Welsch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pascalwelsch.konduit.sample

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.lucasurbas.listitemview.ListItemView
import com.pascalwelsch.arrayadapter.ArrayAdapter
import com.pascalwelsch.konduit.BuildContext
import com.pascalwelsch.konduit.KonduitActivity
import com.pascalwelsch.konduit.KonduitPresenter
import com.pascalwelsch.konduit.KonduitView
import com.pascalwelsch.konduit.sample.SamplesAdapter.SampleItemVH
import com.pascalwelsch.konduit.sample.alert.AlertActivity
import com.pascalwelsch.konduit.sample.input.InputActivity
import com.pascalwelsch.konduit.sample.options.AcceptTosActivity
import com.pascalwelsch.konduit.widget.ListWidget
import com.pascalwelsch.konduit.widget.Widget
import com.pascalwelsch.konduit.widget.listView
import com.pascalwelsch.konduit.widget.widgetList

class MainActivity : KonduitActivity<MainPresenter, MainView>(), MainView {

    private val samplesAdapter = SamplesAdapter()

    override fun providePresenter() = MainPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // setup the RecyclerView and bind it to the samplesAdapter. Konduit doesn't care how the items are presented
        findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
            adapter = samplesAdapter
            setHasFixedSize(true)
        }

        // ListWidget can't be bound to a RecyclerView directly, instead it has to be manually bound to a
        // RecyclerView.Adapter. The ListWidget contains the list of items (immutable if possible). The ListWidget
        // changes whenever one of it's items change.
        bind<ListWidget<Sample>>(R.id.recycler_view, onChange = {
            // This adapter implementation uses swap to replace all items with DiffUtils
            samplesAdapter.swap(it.items)

            // Don't forget to forwards clicks back to the presenter
            samplesAdapter.onItemClickListener = it.onItemClick
        })
    }

    override fun openSample(activityClass: Class<*>) {
        startActivity(Intent(this@MainActivity, activityClass))
    }
}

/**
 * RecyclerView boilerplate adapter with a click listener, displays material design [ListItemView]s
 */
class SamplesAdapter : ArrayAdapter<Sample, SampleItemVH>() {

    class SampleItemVH(val listItemView: ListItemView) : RecyclerView.ViewHolder(listItemView)

    var onItemClickListener: ((Sample) -> Unit)? = null

    override fun onBindViewHolder(holder: SampleItemVH, position: Int) {
        val sample = getItem(position)!!
        holder.listItemView.apply {
            title = sample.name
            subtitle = sample.description
            setOnClickListener { onItemClickListener?.invoke(sample) }
        }
    }

    override fun getItemId(item: Sample): Any? = item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SampleItemVH(
            ListItemView(parent.context).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            })
}

interface MainView : KonduitView {

    /**
     * opens the Sample in a new Activity
     */
    fun openSample(activityClass: Class<*>)
}

class MainPresenter : KonduitPresenter<MainView>() {

    private val samples = listOf(
            Sample("Input", "demonstrate two-way binding of an EditText", InputActivity::class.java),
            Sample("OptionsMenu", "Toggle items in options menu, manual binding",
                    AcceptTosActivity::class.java),
            Sample("Dialog",
                    "show a dialog which survives orientation changes without Fragments, dynamic creation of views",
                    AlertActivity::class.java)
    )

    override fun build(context: BuildContext): List<Widget> = widgetList {
        listView<Sample> {
            key = R.id.recycler_view
            items = samples
            onItemClick = ::onSampleTapped
        }
    }

    private fun onSampleTapped(sample: Sample) {
        viewOrThrow.openSample(sample.activityClass)
    }
}

data class Sample(val name: String, val description: String?, val activityClass: Class<*>)