import 'package:flutter/material.dart';
import 'package:mobile_map_core/mobile_map_core.dart';
import 'package:mobile_map_core_example/SessionManager.dart';

import 'LoginScreen.dart';
/// description:
/// project: mobile_map_core
/// @package: example.lib
/// @author: dammyololade
/// created on: 29/07/2020

class LandingPageScreen extends StatefulWidget {
  @override
  _LandingPageScreenState createState() => _LandingPageScreenState();
}

class _LandingPageScreenState extends State<LandingPageScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[

              RaisedButton(
                onPressed: () async {
                  MobileMapCore.loadMap({
                    "app_type":  SessionManager.userType.toLowerCase(),
                    "user_type_and_id": "customerId=1190",
                    "token": SessionManager.token,
                    "id": SessionManager.userId,
                    "user_first_name": "Olanrewaju",
                    "user_name": SessionManager.name
                  });
                },
                child: Text("Open map"),
              ),

              SizedBox(
                height: 100,
              ),

              FlatButton(onPressed: (){
                SessionManager.logOut();
                Navigator.of(context).pushReplacement(MaterialPageRoute(
                    builder: (ct) => LoginScreen()));
              }, child: Text("Logout"))
            ],
          ),
        ));
  }
}
