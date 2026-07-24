#include <jni.h>
#include <oboe/Oboe.h>

class NoiseEngine : public oboe::AudioStreamDataCallback {
private:
    oboe::ManagedStream mStream;

public:
    bool start() {
        oboe::AudioStreamBuilder builder;
        builder.setDirection(oboe::Direction::Output)
               ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
               ->setSharingMode(oboe::SharingMode::Exclusive)
               ->setFormat(oboe::AudioFormat::Float)
               ->setChannelCount(oboe::ChannelCount::Mono)
               ->setSampleRate(44100)
               ->setDataCallback(this);

        oboe::Result result = builder.openManagedStream(mStream);
        if (result != oboe::Result::OK) {
            return false;
        }

        result = mStream->requestStart();
        return result == oboe::Result::OK;
    }

    void stop() {
        if (mStream) {
            mStream->requestStop();
            mStream->close();
        }
    }

    // Yeh callback har audio frame par call hoga aur buffer ko zero (silence) karega
    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) override {
        float *outputBuffer = static_cast<float *>(audioData);
        
        // Zero Noise / Complete Silence (Saare frames ko 0.0f set kar diya)
        for (int i = 0; i < numFrames; ++i) {
            outputBuffer[i] = 0.0f;
        }
        
        return oboe::DataCallbackResult::Continue;
    }
};

static NoiseEngine *engine = nullptr;

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_noise_app_MainActivity_startAudioEngine(JNIEnv *env, jobject thiz) {
    if (engine == nullptr) {
        engine = new NoiseEngine();
    }
    return engine->start();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_noise_app_MainActivity_stopAudioEngine(JNIEnv *env, jobject thiz) {
    if (engine != nullptr) {
        engine->stop();
        delete engine;
        engine = nullptr;
    }
}
