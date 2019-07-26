package cn.cerc.summer.android.forms.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elves.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.cerc.summer.android.core.Constans;
import cn.cerc.summer.android.core.FileUtils;

/**
 * Created by yangtaiyu on 2017/10/23.
 */

public class NavigationChatImageView extends View implements View.OnClickListener {
    public static String CACHE_FILE = "cacheFiles";
    public static String IMAGE_STARTIP = "startup"; //广告图片缓存
    String resp = null;
    private Context mContext;
    private View view;
    //图片放置
    private ViewPager viewPager;
    //图片放置
    private List<ImageView> imageList;
    // 放4个小灰点的线性布局
    private LinearLayout linearLayout;
    private ImageView lan_Iv;
    //小点之间的距离
    private int pointWidth;
    private TextView startText;
    private RelativeLayout start_relative;
    private List<String> imagePathList = new ArrayList<String>();
    private SharedPreferences settings;  //共享参数
    private JSONObject json = null;
    private String imageFileName = "cacheimage/";
    private String imageFilePath = null;
    private ImageViewPagerListener imageViewPagerListener;
    private boolean misScrolled;

    public NavigationChatImageView(final Activity context, String resp, SharedPreferences setting) {
        super(context);
        this.resp = resp;
        try {
            this.json = new JSONObject(resp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mContext = context;
        settings = setting;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.navigation_chat_frm_item, null);
        lan_Iv = (ImageView) view.findViewById(R.id.lan_Iv);
        linearLayout = (LinearLayout) view.findViewById(R.id.ll);
        startText = (TextView) view.findViewById(R.id.startbtn);
        viewPager = (ViewPager) view.findViewById(R.id.vp);
        start_relative = (RelativeLayout) view.findViewById(R.id.start_relative);
        imageList = new ArrayList<ImageView>();
        startText.setOnClickListener(this);
    }

    public View loadNavigationImage() {
        loadimageViewPager();
        return view;
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.startbtn) {
            //点击开始体验
            imageViewPagerListener.onPopSelected(0);
        }
    }

    public void setPopListener(ImageViewPagerListener listener) {
        imageViewPagerListener = listener;
    }

    private void loadimageViewPager() {
        boolean isfer = settings.getBoolean(Constans.IS_FIRST_SHAREDKEY, true);
        String cacheFile = settings.getString(CACHE_FILE, null);
        if (json != null) {
            if (json.has("startupImage")) {
                try {
                    int imageNewAd = 0;
                    int imageAd = settings.getInt("IMAGE_AD", -1);
                    String startupImage = json.getString("startupImage");
                    if (!"".equals(startupImage)) {
                        String[] strings = startupImage.split(";");
                        if (strings.length > 1) {
                            startupImage = strings[1];
                            imageNewAd = Integer.parseInt(strings[0]);
                            settings.edit().putInt("IMAGE_AD", imageNewAd).commit();
                        } else {
                            settings.edit().putInt("IMAGE_AD", 0).commit();
                        }
                        if (imageAd != imageNewAd) {
                            String fileName = startupImage.substring(startupImage.lastIndexOf("/") + 1);
                            //存到本地的绝对路径
                            imageFilePath = FileUtils.getCacheFilePath(fileName);
                            File file = new File(fileName);
                            RequestParams entity = new RequestParams(startupImage);
                            entity.setSaveFilePath(imageFilePath);
                            x.http().get(entity, new Callback.CommonCallback<File>() {
                                @Override
                                public void onSuccess(File result) {
                                }

                                @Override
                                public void onError(Throwable ex, boolean isOnCallback) {
                                }

                                @Override
                                public void onCancelled(CancelledException cex) {
                                }

                                @Override
                                public void onFinished() {
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (json.has("adImages")) {
                try {
                    int imagesNumber = settings.getInt("ADIMAGES", -1);
                    int imagesNewsNumber = 0;
                    final JSONArray imageAD = json.getJSONArray("adImages");
                    if (imageAD.length() > 0) {
                        String idImages = imageAD.getString(0);
                        String[] strings = idImages.split(";");
                        if (strings.length > 1) {
                            imagesNewsNumber = Integer.parseInt(strings[0]);
                            settings.edit().putInt("ADIMAGES", imagesNewsNumber).commit();
                        } else {
                            settings.edit().putInt("ADIMAGES", 0).commit();
                        }
                        if (imagesNumber != imagesNewsNumber || isfer) {
                            final int[] finalNum = {0};
                            for (int i = 0; i < imageAD.length(); i++) {
                                String[] adImages = imageAD.getString(i).split(";");
                                String imageUrl;
                                if (adImages.length > 1) {
                                    imageUrl = adImages[1];
                                } else {
                                    imageUrl = imageAD.getString(i);
                                }
                                final String filePath = FileUtils.getCacheFilePath(imageFileName + (i + 1) + ".jpg");
                                File file = new File(imageFileName + (i + 1) + ".jpg");
                                //如果不存在
                                if (!file.exists()) {
                                    //创建
                                    file.mkdirs();
                                }
                                RequestParams entity = new RequestParams(imageUrl);
                                entity.setSaveFilePath(filePath);
                                x.http().get(entity, new Callback.CommonCallback<File>() {
                                    @Override
                                    public void onSuccess(File result) {
                                        if (finalNum[0] == imageAD.length() - 1) {
                                            imageCarsousel();
                                        }
                                        finalNum[0]++;
                                    }

                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                    }

                                    @Override
                                    public void onCancelled(CancelledException cex) {
                                    }

                                    @Override
                                    public void onFinished() {
                                    }
                                });
                            }
                        } else {
                            if (imageFilePath != null) {
                                settings.edit().putString(IMAGE_STARTIP, imageFilePath).commit();
                            }
                            imageViewPagerListener.onPopSelected(100);
                        }
                    } else {
                        if (imageFilePath != null) {
                            settings.edit().putString(IMAGE_STARTIP, imageFilePath).commit();
                        }
                        imageViewPagerListener.onPopSelected(100);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //创建配置文件
            } else {
                if (imageFilePath != null) {
                    settings.edit().putString(IMAGE_STARTIP, imageFilePath).commit();
                }
                imageViewPagerListener.onPopSelected(100);
            }
            settings.edit().putBoolean(Constans.IS_FIRST_SHAREDKEY, false).commit();
            settings.edit().putString(CACHE_FILE, resp).commit();
            if (imageFilePath != null) {
                settings.edit().putString(IMAGE_STARTIP, imageFilePath).commit();
            }
        } else {
            imageViewPagerListener.onPopSelected(100);
        }
    }

    private void imageCarsousel() {
        //显示广告轮播图
        imagePathList = getImagePathFromSD(FileUtils.getCacheFilePath(imageFileName));
        //初始化图片和小点，图片的个数和小点的个数要始终一致
        start_relative.setVisibility(View.VISIBLE);
        for (int i = 0; i < imagePathList.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageList.add(imageView);
            // 根据图片的个数去放置相应数量的小灰点
            ImageView huiImageView = new ImageView(mContext);
            huiImageView.setImageResource(R.mipmap.white_bg);
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            huiImageView.setLayoutParams(layoutParams);

            if (i > 0) {
                //给除了第一个小点的其它小点左边距
                layoutParams.leftMargin = 20;
            }

            linearLayout.addView(huiImageView);
        }

        lan_Iv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //视图全部绘制完成时，计算得到小点之间的距离
                if (imagePathList.size() < 2) {
                    startText.setText("进入系统");
                } else {
                    pointWidth = linearLayout.getChildAt(1).getLeft() - linearLayout.getChildAt(0).getLeft();
                    System.out.println(pointWidth);
                }
            }
        });
        //绑定适配器
        viewPager.setAdapter(new MyAdapter());
        //设置图片切换的监听事件
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //让滑到最后一页显示按钮
                if (position == imagePathList.size() - 1) {
                    startText.setVisibility(VISIBLE);
                } else {
                    startText.setVisibility(GONE);
                }
            }

            @Override
            //在Viewpger的界面不断滑动的时候会触发
            //position：当前滑到了第几页从0开始计数
            public void onPageScrolled(int position, float offset, int arg2) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lan_Iv.getLayoutParams();
                //滑动的百分比乘上小点之间的距离，再加上当前位置乘上距离，即为蓝色小点的左边距
                layoutParams.leftMargin = (int) (pointWidth * offset + position * pointWidth);
                lan_Iv.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        misScrolled = false;
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        misScrolled = true;
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !misScrolled) {
                            imageViewPagerListener.onPopSelected(0);
                        }
                        misScrolled = true;
                        break;
                }
            }
        });
    }

    /**
     * 从sd卡获取图片资源
     *
     * @return
     */
    private List<String> getImagePathFromSD(String filePath) {
        // 图片列表
        List<String> imagePathList = new ArrayList<String>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (checkIsImageFile(file.getPath())) {

                imagePathList.add(file.getPath());
            }
        }
        // 返回得到的图片列表
        Collections.sort(imagePathList, new ImageComparator());
        return imagePathList;
    }

    /**
     * 根据路径获取文件名
     *
     * @param pathandname
     * @return
     */
    public String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1, end);
        } else {
            return null;
        }
    }

    /**
     * 检查扩展名，得到图片格式的文件
     *
     * @param fName 文件名
     * @return
     */
    private boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg") || FileEnd.equals("bmp")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }

    //回调接口定义
    public interface ImageViewPagerListener {
        public void onPopSelected(int time);
    }

    /**
     * 自定义名称排序
     */
    class ImageComparator implements Comparator<String> {

        @Override
        public int compare(String lhs, String rhs) {
            if ((getFileName(lhs)).compareTo(getFileName(rhs)) > 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    class MyAdapter extends PagerAdapter {
        @Override
        // 返回ViewPager里面有几页
        public int getCount() {
            return imagePathList.size();
        }

        @Override
        // 用于判断是否有对象生成界面
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = imageList.get(position);
            imageView.setImageURI(Uri.fromFile(new File(imagePathList.get(position))));
            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}
