package ade.dicoding.sub2.ui.tvshow


import ade.dicoding.sub2.R
import ade.dicoding.sub2.data.local.entity.TiviesEntity
import ade.dicoding.sub2.util.gone
import ade.dicoding.sub2.viewmodel.ViewModelFactory
import ade.dicoding.sub2.vo.Status
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_movie.*

/**
 * A simple [Fragment] subclass.
 */
class TvFragment : Fragment() {
    private lateinit var viewModel: TVViewModel
    private lateinit var adapter: TVShowAdapter
    private lateinit var tivies: MutableList<TiviesEntity>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null) {
            coll_lay.gone()
            val factory = ViewModelFactory.getInstance(activity?.application)
            viewModel = ViewModelProviders.of(activity!!, factory).get(TVViewModel::class.java)
            tivies = mutableListOf()
            initAdapter()
            setSearching()
            viewModel.setUsername("test2")
            viewModel.tivies.observe(this) { response ->
                Log.e("response", "$response")
                if (response != null) {
                    when (response.status) {
                        Status.LOADING -> {

                        }
                        Status.ERROR -> {

                        }
                        Status.SUCCESS -> {
                            tivies.clear()
                            response.apply {
                                if (!data.isNullOrEmpty()) {
                                    tivies.addAll(data as MutableList<TiviesEntity>)
                                }

                            }
                            adapter.notifyDataSetChanged()
                        }
                    }
                }

            }

        }
    }

    private fun initAdapter() {
        adapter = TVShowAdapter(activity, tivies)
        rv_list.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = this@TvFragment.adapter
        }
    }

    private fun setSearching() {
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
        })
    }


    companion object {
        fun newInstance(): Fragment {
            return TvFragment()
        }
    }


}
