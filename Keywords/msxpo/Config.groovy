package msxpo

/**
 * Central settings with a safe resolution order:
 *   1. Katalon GlobalVariable (if you declare it in Profiles -> default)
 *   2. JVM system property  (-Dmsxpo.baseUrl=...  etc.)
 *   3. Built-in defaults below
 *
 * This keeps the suite runnable on a fresh import WITHOUT any profile setup,
 * while still letting CI override credentials/URL without editing code.
 *
 * Declared overrides (optional), all read as GlobalVariable first:
 *   BASE_URL, ADMIN_EMAIL, ADMIN_PASSWORD, MEDIA_FILE
 */
class Config {

	static final String DEF_BASE_URL        = 'https://app.msxpo.sa'
	static final String DEF_ADMIN_EMAIL     = 'aartioza2026+fgate@gmail.com'
	static final String DEF_ADMIN_PASSWORD  = 'Admin@123'
	static final String DEF_MEDIA_FILE      =
		'C:\\Users\\admin\\Desktop\\growtech\\media\\360_F_826675388_WobphZ8oJEt8nKn9IsoxihUHRuZZSDqB.jpg'

	static String resolve(String globalVarName, String sysProp, String defaultValue) {
		try {
			def v = internal.GlobalVariable."${globalVarName}"
			if (v != null && v.toString().trim()) {
				return v.toString()
			}
		} catch (MissingPropertyException ignored) {
			// GlobalVariable not declared in the active profile -> fall through
		}
		String sp = System.getProperty(sysProp)
		if (sp != null && sp.trim()) {
			return sp
		}
		return defaultValue
	}

	static String baseUrl()        { return resolve('BASE_URL', 'msxpo.baseUrl', DEF_BASE_URL) }
	static String adminEmail()     { return resolve('ADMIN_EMAIL', 'msxpo.adminEmail', DEF_ADMIN_EMAIL) }
	static String adminPassword()  { return resolve('ADMIN_PASSWORD', 'msxpo.adminPassword', DEF_ADMIN_PASSWORD) }
	static String mediaFile()      { return resolve('MEDIA_FILE', 'msxpo.mediaFile', DEF_MEDIA_FILE) }
}
