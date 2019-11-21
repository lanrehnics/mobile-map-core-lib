import 'dart:async';

import 'package:flutter/services.dart';

class MobileMapCore {
  static const MethodChannel _channel = const MethodChannel('mobile_map_core');

  static Future<String> get prepareMap async {
    final String status = await _channel.invokeMethod('prepareMap');
    return status;
  }
}
