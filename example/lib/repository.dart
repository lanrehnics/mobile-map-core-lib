import 'dart:convert';

import 'package:http/http.dart' as http;

/// description:
/// project: mobile_map_core
/// @package: example.lib
/// @author: dammyololade
/// created on: 30/07/2020
class Repository {
  var baseUrl = "https://stage.api.kobo360.com/";

  Future<UserModel> login(String type, String email, String password) async {
    try {
      String url = "${baseUrl}user/auth";
      var response = await http.post(url,
          body: {'secret': password, 'email': email, 'user_type': type});
      print('Response status: ${response.statusCode}');
      print('Response body: ${response.body}');
      var decodeRes =  jsonDecode(response.body);
      if (response.statusCode == 200) {
        var token = decodeRes["data"]["token"];
        print(token);
        return decodeToken(token, type);
      } else {
        return Future.error(decodeRes["message"] ?? response.reasonPhrase);
      }
    } catch (err) {
      return Future.error(err);
    }
  }

  Future<UserModel> decodeToken(String token, String type) async {
    try {
      String url = "${baseUrl}user/decodeToken";
      var response = await http.post(url, body: {'token': token});
      print('Response status: ${response.statusCode}');
      print('Response body: ${response.body}');
      if (response.statusCode == 200) {
        var data = jsonDecode(response.body)["data"];
        print(data);
        return UserModel(data["email"], "${data["firstName"]} ${data["lastName"]}", token,
            data["${type.toLowerCase()}Id"], type);
      }
    } catch (err) {
      return Future.error(err);
    }
  }
}

class UserModel {
  String email;
  String name, token, type;
  int id;

  UserModel(this.email, this.name, this.token, this.id, this.type);
}
