package com.matveyev.buyfaster

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.matveyev.buyfaster.uicomponents.ObserverListAdapter
import com.matveyev.buyfaster.vms.DepartmentVM
import com.matveyev.buyfaster.vms.SubjectVM
import kotlinx.android.synthetic.main.dialog_add_subject.view.*

class AddSubjectDialogFragment(
    private val departments: ObservableArrayList<DepartmentVM>
) : DialogFragment() {
    lateinit var positive: DialogInterface.OnClickListener
    lateinit var viewModel: AddSubjectViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = LayoutInflater
                .from(it)
                .inflate(R.layout.dialog_add_subject, null)

            viewModel = ViewModelProviders.of(it).get(AddSubjectViewModel::class.java)
            viewModel.subject = SubjectVM()
            viewModel.departmentsAutoComplete = departments
            val app = it.application as BuyFasterApp
            viewModel.depsRepo = app.depsRepository.value

            val adapter = ObserverListAdapter(
                departments,
                R.layout.department_autocomplete,
                viewModel) { view.department.refreshAutoCompleteResults() }
            view.department.setAdapter(adapter)

            val binding: ViewDataBinding? = DataBindingUtil.bind(view)
            binding?.run {
                setVariable(BR.viewModel, viewModel)
                executePendingBindings()
            }
            builder.setView(view)
                .setPositiveButton(R.string.Remember, positive)
                .setNegativeButton(R.string.Nevermind) { dialog, _ -> dialog.cancel() }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}