import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:mobile_map_core/mobile_map_core.dart';
import 'package:mobile_map_core_example/LandingPage.dart';
import 'package:mobile_map_core_example/LoginScreen.dart';
import 'package:mobile_map_core_example/SessionManager.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await SessionManager.init();

  runApp(MyApp());
}

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
    //initPlatformState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: SessionManager.isLoggedIn ? LandingPageScreen() : LoginScreen(),
    );
  }
}
