import 'dart:async';

import 'package:flutter/services.dart';

class MobileMapCore {
  static const MethodChannel _channel =
      const MethodChannel('mobile_map_core');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
