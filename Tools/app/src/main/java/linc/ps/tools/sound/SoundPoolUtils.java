package linc.ps.tools.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


import java.util.HashMap;

import linc.ps.tools.R;
import linc.ps.tools.base.MyApplication;

/**
 * Created by Frank on 2017/1/11.
 * 声音文件-->用于播放提示语
 */
public class SoundPoolUtils {

    int streamVolume;// 音效的音量
    private SoundPool soundPool;// 定义SoundPool 对象
    private HashMap<Integer, Integer> soundPoolMap;// 定义HASH表


    //获取单例
    public static SoundPoolUtils getInstance(){
        return SoundPoolUtils.SingletonHolder.INSTANCE;
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final SoundPoolUtils INSTANCE = new SoundPoolUtils();
    }

    // 构造方法私有
    private SoundPoolUtils() {
        if (soundPool == null){
            // 初始化soundPool 对象,第一个参数是允许有多少个声音流同时播放,第2个参数是声音类型,第三个参数是声音的品质
            soundPool = new SoundPool(100, AudioManager.STREAM_MUSIC, 100);
            // 初始化HASH表
            soundPoolMap = new HashMap<Integer, Integer>();
            // 获得声音设备和设备音量
            AudioManager mgr = (AudioManager) MyApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
            streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
            loadallSfx();
        }
    }

    public void loadSfx(int raw, int ID) {
        // 把资源中的音效加载到指定的ID(播放的时候就对应到这个ID播放就行了)
        soundPoolMap.put(ID, soundPool.load(MyApplication.getContext(), raw, ID));
    }

    /**
     * play(1, 0); // 默认,警告提示音
     * play(2, 0); // 成功弹出提示音
     */
    public void play(int sound, int uLoop) {
        soundPool.play(soundPoolMap.get(sound), streamVolume, streamVolume, 1, uLoop, 1f);
    }

    public void loadallSfx(){
        // 先做特殊判断
        String where = "四川";
        if(where.equals("四川")){
            // 加载默认的声音
            loadSfx(R.raw.sound_default_1, 1);

        }else if(where.equals("河北")){
            // 其他地方话
            loadSfx(R.raw.sound_default_1, 1);
        }
    }
}
