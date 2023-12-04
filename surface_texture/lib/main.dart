import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MainApp());
}

final class SurfaceTexturePlugin {
  static const _channel = MethodChannel('surface_texture');

  static Future<int?> generateSurfaceTexture({int height = 0}) async {
    return await _channel.invokeMethod(
      'generateSurfaceTexture',
      {'height': height},
    );
  }

  static Future<void> setSurfaceBufferSize({int height = 0}) async {
    return await _channel.invokeMethod(
      'setSurfaceBufferSize',
      {'height': height},
    );
  }
}

final class MainApp extends StatefulWidget {
  const MainApp({super.key});

  @override
  State<MainApp> createState() => _MainAppState();
}

final class _MainAppState extends State<MainApp> {
  int? _textureId;
  double _height = 500;

  @override
  void initState() {
    super.initState();
    _initTextureState();
  }

  Future<void> _initTextureState() async {
    int? textureId;

    try {
      textureId = await SurfaceTexturePlugin.generateSurfaceTexture(
        height: _height.toInt(),
      );
    } on PlatformException {
      debugPrint('Failed to generate surface texture.');
    }

    if (!mounted) {
      return;
    }

    setState(() {
      _textureId = textureId;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
          appBar: AppBar(
            title: const Text('Surface Texture Demo'),
          ),
          body: _textureId == null
              ? const Center(child: CircularProgressIndicator())
              : SingleChildScrollView(
                  child: Column(
                    children: [
                      Text('Texture ID: $_textureId, Height: $_height'),
                      Slider(
                        value: _height,
                        min: 0,
                        max: 1080 * 2,
                        onChanged: (value) {
                          setState(() {
                            _height = value;
                          });
                          SurfaceTexturePlugin.setSurfaceBufferSize(
                            height: _height.toInt(),
                          );
                        },
                      ),
                      _Texture(textureId: _textureId!, height: _height),
                    ],
                  ),
                )),
    );
  }
}

final class _Texture extends StatelessWidget {
  final int _textureId;
  final double _surfaceHeight;

  const _Texture({
    required int textureId,
    double height = 0,
  })  : _textureId = textureId,
        _surfaceHeight = height;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: SizedBox(
        width: 1080,
        height: _surfaceHeight,
        child: Texture(textureId: _textureId),
      ),
    );
  }
}
