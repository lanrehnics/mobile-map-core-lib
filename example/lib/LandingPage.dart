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
  static const String SQUAD_APP = "squad";
  static const String CUSTOMER_APP = "customer";
  static const String TRANSPORTER_APP = "transporter";
  static const String DRIVER_APP = "driver";

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
                    "app_type": SessionManager.userType.toLowerCase(),
                    "user_type_and_id":
                        "${SessionManager.userType.toLowerCase()}Id=${SessionManager.userId}",
                    "token": SessionManager.token,
                    "id": SessionManager.userId,
                    "user_first_name": "Olanrewaju",
                    "user_name": SessionManager.name,
                    "kobo_base_url": "https://stage.api.kobo360.com/",
                    "gps_base_url": "https://stagegpsapi.kobo360.com/v2/",
                    "geo_base_url": "https://stagegpsapi.kobo360.com/v2/",
                    "environment": "staging",
                    "simulate": true
                  });
                },
                child: Text("Open map"),
              ),
              SizedBox(
                height: 100,
              ),
              FlatButton(
                  onPressed: () {
                    SessionManager.logOut();
                    Navigator.of(context).pushReplacement(
                        MaterialPageRoute(builder: (ct) => LoginScreen()));
                  },
                  child: Text("Logout"))
            ],
          ),
        ));
  }
}
