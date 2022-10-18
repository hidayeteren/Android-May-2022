package org.csystem.android.app.multipleactivity

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import org.csystem.android.app.multipleactivity.data.Education
import org.csystem.android.app.multipleactivity.data.MaritalStatus
import org.csystem.android.app.multipleactivity.data.RegisterInfo
import org.csystem.android.app.multipleactivity.data.viewmodel.RegisterInfoViewModel
import org.csystem.android.app.multipleactivity.databinding.ActivityRegisterBinding
import org.csystem.android.app.multipleactivity.datetime.createBirthDate
import org.csystem.android.app.multipleactivity.keys.REGISTER_INFO
import org.csystem.android.app.multipleactivity.keys.TEXT_PASSWORD_HIDE
import org.csystem.android.app.multipleactivity.keys.TEXT_PASSWORD_SHOW
import java.time.DateTimeException
import java.time.LocalDate
import java.time.Month
import java.util.function.ToLongBiFunction

class RegisterActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityRegisterBinding

    private fun getEducation() : Education
    {
        val selected = findViewById<RadioButton>(mBinding.registerActivityRadioGroupEducation.checkedRadioButtonId)

        return Education.valueOf(selected.tag as String)
    }

    private fun getMaritalStatus() : MaritalStatus
    {
        val selected = findViewById<RadioButton>(mBinding.registerActivityRadioGroupMaritalStatus.checkedRadioButtonId)

        return MaritalStatus.valueOf(selected.tag as String)
    }

    private fun confirmPassword() = mBinding.registerViewModel!!.password == mBinding.registerViewModel!!.confirmPassword

    private fun registerButtonClickedCallback()
    {
        if (confirmPassword()) {
            val day = mBinding.registerActivitySpinnerDays.selectedItemPosition + 1
            val month = mBinding.registerActivitySpinnerMonths.selectedItemPosition + 1
            val year = mBinding.registerActivitySpinnerYears.selectedItem as Int
            val birthDate = createBirthDate(day, month, year);

            if (birthDate == null) {
                Toast.makeText(this, R.string.invalid_date_toast_message_text, Toast.LENGTH_LONG).show()
                return
            }

            Intent(this, RegisterDetailsActivity::class.java).apply {
                putExtra(REGISTER_INFO, mBinding.registerViewModel)
                startActivity(this)
            }
        }
        else
            Toast.makeText(this, "Confirm your password", Toast.LENGTH_LONG).show()
    }

    private fun showPasswordSwitchCallback(checked: Boolean)
    {
        mBinding.registerActivityEditTextPassword.also {
            it.inputType = if (checked) TEXT_PASSWORD_SHOW else TEXT_PASSWORD_HIDE
        }
    }

    private fun initDaySpinner(today: LocalDate)
    {
        val days = Array(31) {it + 1}

        mBinding.registerActivitySpinnerDays.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        mBinding.registerActivitySpinnerDays.setSelection(today.dayOfMonth - 1)
    }

    private fun initMonthSpinner(today: LocalDate)
    {
        mBinding.registerActivitySpinnerMonths.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Month.values())
        mBinding.registerActivitySpinnerMonths.setSelection(today.monthValue - 1)
    }

    private fun initYearSpinner(today: LocalDate)
    {
        val first = today.year - 100 + 1
        val years = Array(100) {first + it}.reversedArray()

        mBinding.registerActivitySpinnerYears.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
    }

    private fun initBirthDateSpinners()
    {
        val today = LocalDate.now() //Sistemin saati değiştirilmesine karşılık bir server'dan alınabilir. İleride göreceğiz
        initDaySpinner(today)
        initMonthSpinner(today)
        initYearSpinner(today)
    }

    private fun initAcceptSwitch()
    {
        mBinding.registerActivitySwitchAccept.setOnCheckedChangeListener {
                _, checked -> mBinding.registerActivityButtonRegister.isEnabled = checked
        }
    }

    private fun initShowPasswordSwitch()
    {
        mBinding.registerActivitySwitchShowPassword.setOnCheckedChangeListener {
                _, checked -> showPasswordSwitchCallback(checked)
        }
    }
    private fun initRegisterButton()
    {
        mBinding.registerActivityButtonRegister.setOnClickListener{registerButtonClickedCallback()}
    }

    private fun initViews()
    {
        initRegisterButton()
        initAcceptSwitch()
        initShowPasswordSwitch()
        initBirthDateSpinners()
    }

    private fun initBinding()
    {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        mBinding.registerViewModel = RegisterInfoViewModel()
    }

    private fun initialize()
    {
        initBinding()
        initViews()
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initialize()
    }
}