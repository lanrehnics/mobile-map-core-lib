import 'dart:async';

import 'package:flutter/services.dart';

class MobileMapCore {
  static const MethodChannel _channel = const MethodChannel('mobile_map_core');

  static loadMap(Map<String, dynamic> data) async {
    await _channel.invokeMethod("launch_map", data);
  }
}
