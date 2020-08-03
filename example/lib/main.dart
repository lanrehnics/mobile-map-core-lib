import 'package:flutter/material.dart';
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
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: SessionManager.isLoggedIn ? LandingPageScreen() : LoginScreen(),
    );
  }
}
