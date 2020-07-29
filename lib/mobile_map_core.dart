import 'dart:async';

import 'package:flutter/services.dart';

class MobileMapCore {
  static const MethodChannel _channel = const MethodChannel('mobile_map_core');

  static const String LAUNCH_MAP_METHOD = "launch_map";
  static const String NAVIGATE_DRIVER_METHOD = "navigate_driver";

  static loadMap(Map<String, dynamic> data, {String method = LAUNCH_MAP_METHOD}) async {
    await _channel.invokeMethod(method, data);
  }

}
