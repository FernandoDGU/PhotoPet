package com.fcfm.photopet.controller

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fcfm.photopet.controller.Fragment.Fragment_Busqueda
import com.fcfm.photopet.controller.Fragment.Fragment_Inicio
import com.fcfm.photopet.controller.Fragment.Fragment_Perfil
import com.fcfm.photopet.R
import com.fcfm.photopet.utils.loggedUser
import com.google.android.material.tabs.TabLayout

class FragmentsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        supportActionBar?.hide()
        val tabLayout = findViewById(R.id.tabLayaout) as TabLayout
        //Tabs from fragment
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 ->{
                        changeFragment(Fragment_Inicio(), "Inicio")
                    }
                    1 -> {
                        changeFragment(Fragment_Busqueda(), "Busqueda")
                    }
                    2 -> {
                        changeFragment(Fragment_Perfil(), "Perfil")
                    }
                    else -> changeFragment(Fragment_Inicio(), "Inicio")
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }


        })
        //El que se abre por defecto
        tabLayout.selectTab(tabLayout.getTabAt(0))
        changeFragment(Fragment_Inicio(), "Inicio")

    }
    private fun changeFragment(fragment: Fragment, tag: String){
        val currentFragment = supportFragmentManager.findFragmentByTag(tag)

        //Si no existe
        if(currentFragment == null || currentFragment.isVisible.not()){
            //Se cambia el fragmento
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment, tag)
                .commit()
        }
    }

}