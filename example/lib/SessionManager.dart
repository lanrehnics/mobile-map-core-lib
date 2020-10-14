import 'package:shared_preferences/shared_preferences.dart';
/// description:
/// project: mobile_map_core
/// @package: example.lib
/// @author: dammyololade
/// created on: 30/07/2020
class SessionManager{

  static SharedPreferences sharedPreferences;


  static Future<Null> init() async {
    sharedPreferences = await SharedPreferences.getInstance();
  }

  static const String KEY_ID = 'id';
  static const String KEY_TOKEN = 'token';
  static const String KEY_NAME = 'name';
  static const String KEY_USER_TYPE= 'type';
  static const String KEY_ISLOGGED_IN= 'logged_in';


  static int get userId => sharedPreferences.getInt(KEY_ID) ?? 1190;

  static set userId(int id) => sharedPreferences.setInt(KEY_ID, id);

  static String get token => sharedPreferences.getString(KEY_TOKEN) ?? '';

  static set token(String value) =>
      sharedPreferences.setString(KEY_TOKEN, value);

  static String get name => sharedPreferences.getString(KEY_NAME) ?? 'olarenwaju';

  static set name(String value) =>
      sharedPreferences.setString(KEY_NAME, value);

  static String get userType => sharedPreferences.getString(KEY_USER_TYPE) ?? 'customer';

  static set userType(String value) =>
      sharedPreferences.setString(KEY_USER_TYPE, value);

  static set isLoggedIn(bool loggedIn) {
    sharedPreferences.setBool(KEY_ISLOGGED_IN, loggedIn);
  }

  static bool get isLoggedIn =>
      sharedPreferences.getBool(KEY_ISLOGGED_IN) ?? false;

  static Future<bool> logOut() async {
    return await sharedPreferences.clear();
  }
}