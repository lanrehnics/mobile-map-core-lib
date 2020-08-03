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

  String token =
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOjk3OTQsInVzZXJUeXBlIjoiYWRtaW4iLCJlbWFpbCI6InRveWVAa29ibzM2MC5jb20iLCJtb2JpbGUiOiIrMjM0ODA2NjYwMzA2MyIsImZpcnN0TmFtZSI6IlRveWUiLCJsYXN0TmFtZSI6IkZhdG9sdSIsIm1vYmlsZVZlcmlmaWVkIjowLCJlbWFpbFZlcmlmaWVkIjowLCJpbWFnZSI6bnVsbCwicHVzaF90b2tlbiI6IjEyMzRwY3QiLCJyb2xlIjoiU3VwZXJBZG1pbiIsImFkbWluSWQiOjEyMiwiZGVzaWduYXRpb24iOiJQb3AiLCJyb2xlcyI6WyJDdXN0b21lciIsIlRlY2hBZG1pbiIsIlN1cGVyQWRtaW4iXSwiYnVzaW5lc3NVbml0IjpbeyJvZmZpY2VyVHlwZSI6IlN1cHBseSBDb29yZGluYXRvciIsImJ1c2luZXNzVW5pdElkIjoxMDcsImJ1c2luZXNzVW5pdE5hbWUiOiJ2Z2piIn0seyJvZmZpY2VyVHlwZSI6Ik9wZXJhdGlvbnMgQ29vcmRpbmF0b3IiLCJidXNpbmVzc1VuaXRJZCI6MTA4LCJidXNpbmVzc1VuaXROYW1lIjoidGVzdG9uc3RhZ2UifSx7Im9mZmljZXJUeXBlIjoiV2F5YmlsbCBDb2xsZWN0b3IiLCJidXNpbmVzc1VuaXRJZCI6MTAyLCJidXNpbmVzc1VuaXROYW1lIjoiR29MaXZlVG9kYXkifSx7Im9mZmljZXJUeXBlIjoiU3VwcGx5IENvb3JkaW5hdG9yIiwiYnVzaW5lc3NVbml0SWQiOjExMiwiYnVzaW5lc3NVbml0TmFtZSI6Ik9sYW0ifV0sInVuaXF1ZUhhc2giOiI1ZjI4NWU4NjJhYTIxIiwiaXNzIjoiS29ibzM2MCIsImlhdCI6MTU5NjQ4MTE1OCwiZXhwIjoxNTk3MDg1OTU4fQ.vZaUNDZToVhOqt2yY4xYek7-GNHPNiggzKidNcg6Rh0";
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
        "token": token,
        "id": 1190,
        "user_name": "Olanrewaju",
        "geo_base_url": "https://stagegpsapi.kobo360.com/v2/",
        "kobo_base_url": "https://stage.api.kobo360.com/",
        "simulate_route_for_driver": true
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
                      "token": token,
                      "id": 1190,
                      "user_name": "Olanrewaju",
                      "geo_base_url": "https://stagegpsapi.kobo360.com/v2/",
                      "kobo_base_url": "https://stage.api.kobo360.com/",
                      "simulate_route_for_driver": true
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
