import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.preference.PreferenceManager
import java.util.Locale


//object LocaleHelper {
//    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
//    fun onAttach(context: Context): Context {
//        val lang = getPersistedData(context, Locale.getDefault().language)
//        return setLocale(context, lang)
//    }
//
//    fun getLanguage(context: Context): String? {
//        return getPersistedData(context, Locale.getDefault().language)
//    }
//
//    private fun setLocale(context: Context, language: String?): Context {
//        persist(context, language)
//        return updateResourcesLegacy(context, language)
//    }
//
//    private fun getPersistedData(context: Context, defaultLanguage: String): String? {
//        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)
//    }
//
//    private fun persist(context: Context, language: String?) {
//        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//        val dat
//        val editor = preferences.edit()
//        editor.putString(SELECTED_LANGUAGE, language)
//        editor.apply()
//    }
//
//    private fun updateResources(context: Context, language: String?): Context {
//        val locale = Locale(language)
//        Locale.setDefault(locale)
//        val configuration: Configuration = context.resources.configuration
//        configuration.setLocale(locale)
//        configuration.setLayoutDirection(locale)
//        return context.createConfigurationContext(configuration)
//    }
//
//    @Suppress("deprecation")
//    private fun updateResourcesLegacy(context: Context, language: String?): Context {
//        val locale = Locale(language)
//        Locale.setDefault(locale)
//        val resources = context.resources
//        val configuration: Configuration = resources.configuration
//        configuration.locale = locale
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            configuration.setLayoutDirection(locale)
//        }
//        resources.updateConfiguration(configuration, resources.displayMetrics)
//        return context
//    }
//}