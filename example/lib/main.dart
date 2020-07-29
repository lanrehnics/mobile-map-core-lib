import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:mobile_map_core/mobile_map_core.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String stuff = "nvlkxjvl";

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      var config = {
        "app_type": "customer",
        "user_type_and_id": "customerId=1190",
        "token": "token",
        "id": 1190,
        "user_name": "Olanrewaju"
      };
      platformVersion = await MobileMapCore.loadMap(config);
    } on PlatformException {
      platformVersion = 'Failed to prepare map.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
          appBar: AppBar(
            title: const Text('Plugin example app'),
          ),
          body: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Text('Running on: $_platformVersion\n'),
                SizedBox(
                  height: 50,
                ),
                RaisedButton(
                  onPressed: () async {
                    MobileMapCore.loadMap({
                      "app_type": "customer",
                      "user_type_and_id": "customerId=1190",
                      "token": "token",
                      "id": 1190,
                      "user_name": "Olanrewaju"
                    });
                  },
                  child: Text("Open map"),
                )
              ],
            ),
          )),
    );
  }
}
