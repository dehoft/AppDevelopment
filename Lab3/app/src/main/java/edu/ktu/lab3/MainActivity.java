package edu.ktu.lab3;

import android.app.Activity;
import android.content.Intent;
import android.view.Surface;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    private Context context = this;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private LocationManager locationManager;

    private Button startAndStop;
    private Button compass;

    private float xVal=0;
    private float yVal=0;
    private float zVal=0;
    private TextView xValue;
    private TextView yValue;
    private TextView zValue;
    private TextView coordinates;

    private boolean informationObtained;

    private static final String TAG= "AndroidCameraApi";
    private Button takePictureButton;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION= 200;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    private GPS geoListener;
    private GPS netListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        informationObtained=false;

        compass=(Button) findViewById(R.id.buttonCompass);
        compass.setOnClickListener(showCompass);
        startAndStop=(Button) findViewById(R.id.start_and_stop);
        startAndStop.setOnClickListener(StartAndStopButtonListener);

        xValue=(TextView) findViewById(R.id.x_value);
        yValue=(TextView) findViewById(R.id.y_value);
        zValue=(TextView) findViewById(R.id.z_value);
        coordinates=(TextView) findViewById(R.id.coordinates);

        senSensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer=senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        textureView = (TextureView) findViewById(R.id.textureView);
        assert textureView!=null;
        textureView.setSurfaceTextureListener(textureListener);

        takePictureButton= (Button) findViewById(R.id.take_photo);
        assert takePictureButton !=null;
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });


    }

    public void geoEnable(View view){
        geoListener=new GPS();
        netListener=new GPS();
        try{
            this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, netListener);
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, geoListener);
        } catch (SecurityException e){
            e.printStackTrace();
        }
    }

    public void stopGeo(){
        try{
            locationManager.removeUpdates(netListener);

            locationManager.removeUpdates(geoListener);
            netListener=null;
            geoListener=null;
        } catch (SecurityException e){
            e.printStackTrace();
        }
    }


    View.OnClickListener showCompass= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent =new Intent(context, Compass.class);
            context.startActivity(intent);
        }
    };

    View.OnClickListener StartAndStopButtonListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(senAccelerometer==null){
                Toast.makeText(MainActivity.this,getString(R.string.no_sensor), Toast.LENGTH_LONG).show();
            }

            if (informationObtained){
                startAndStop.setText(getString(R.string.start));
                senSensorManager.unregisterListener(MainActivity.this,senAccelerometer);
                informationObtained=false;
            }else {
                startAndStop.setText(getString(R.string.stop));
                senSensorManager.registerListener(MainActivity.this,senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                informationObtained=true;
            }
        }
    };
    @Override
    public void onSensorChanged(SensorEvent event){
        Sensor mySensor=event.sensor;
        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            if(Math.abs(event.values[0]-xVal)>0.5 || Math.abs(event.values[1]-yVal)>0.5 || Math.abs(event.values[2]-zVal)>0.5 )
            {
                xVal=event.values[0];
                yVal=event.values[1];
                zVal=event.values[2];
                if(-2<xVal && xVal<2 && yVal>8 && -2<zVal && zVal<2){
                    xValue.setText("Standing on the bottom");
                }else if(-2<xVal && xVal<2 && zVal>8 && -2<yVal && yVal<2){
                    xValue.setText("Face up");
                }else if(-2<xVal && xVal<2 && zVal<-8 && -2<yVal && yVal<2){
                    xValue.setText("Face down");
                    finish();
                    System.exit(0);
                }else if(-2<yVal && yVal<2 && xVal<-8 && -2<zVal && zVal<2){
                xValue.setText("On the right side");
                }else if(-2<yVal && yVal<2 && xVal>8 && -2<zVal && zVal<2){
                    xValue.setText("On the left side");
                }else if(-2<xVal && xVal<2 && yVal<-8 && -2<zVal && zVal<2){
                    xValue.setText("Standing on the top");
                }else {
                    xValue.setText("WEIRD");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    protected void onPause(){
        super.onPause();
        if (senAccelerometer!=null)
            senSensorManager.unregisterListener(MainActivity.this,senAccelerometer);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            return;
        }

        this.locationManager.removeUpdates(this);
        stopBackgroundThread();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(senAccelerometer != null && informationObtained)
            senSensorManager.registerListener(MainActivity.this,senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

        startBackgroundThread();
        if (textureView.isAvailable()){
            openCamera();
        }else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    public void onLocationChanged(Location location){
        if (location!=null)
        {
            coordinates.setText(location.getLatitude()
                    + " "+ location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
    }

    @Override
    public void onProviderEnabled(String provider){
    }

    @Override
    public void onProviderDisabled(String provider){
    }

    TextureView.SurfaceTextureListener textureListener =new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private final CameraDevice.StateCallback stateCallback =new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.e(TAG, "onOpened");
            cameraDevice =camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice=null;
        }
    };

    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(MainActivity.this, "Saved:"+ file, Toast.LENGTH_LONG).show();
        }
    };

    protected void startBackgroundThread(){
        mBackgroundThread=new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread(){
        mBackgroundThread.quitSafely();
        try{
            mBackgroundThread.join();
            mBackgroundThread=null;
            mBackgroundHandler=null;
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    protected void takePicture(){
        if(null==cameraDevice){
            Log.e(TAG, "Camera device is null");
            return;
        }

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try{
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes=null;
            if(characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height =480;
            if(jpegSizes!=null && 0 < jpegSizes.length){
                width=jpegSizes[0].getWidth();
                height=jpegSizes[0].getHeight();
            }
            ImageReader reader =ImageReader.newInstance(width,height,ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            int rotation =getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            final File file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try{
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    } catch (IOException e){
                        e.printStackTrace();
                    } finally {
                        if(image != null){
                            image.close();
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException{
                    OutputStream output = null;
                    try{
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    }finally {
                        if (null != output){
                            output.close();
                        }
                    }
                }
            };

            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session,CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(MainActivity.this, "Saved:"+file, Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                }
            };

            cameraDevice.createCaptureSession (outputSurfaces, new CameraCaptureSession.StateCallback(){
                @Override
                public  void onConfigured(CameraCaptureSession session)
                {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e){
                        e.printStackTrace();
                    }
                }
                @Override
                        public void onConfigureFailed(CameraCaptureSession session){
                }
            }, mBackgroundHandler);

        } catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    protected void createCameraPreview(){
        try {
            final SurfaceTexture texture =textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder= cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == cameraDevice){
                        return;
                    }
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Toast.makeText(MainActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        }catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera(){
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");

        try{
            cameraId= manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this, new  String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    protected void updatePreview(){
        if(null ==cameraDevice){
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    private void closeCamera(){
        if(null!=cameraDevice){
            cameraDevice.close();
            cameraDevice=null;
        }
        if (null != imageReader){
            imageReader.close();
            imageReader=null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode==REQUEST_CAMERA_PERMISSION){
            if (grantResults[0]== PackageManager.PERMISSION_DENIED){
                Toast.makeText(MainActivity.this, "Sorry!!!, you can't use this app without granting permissions", Toast.LENGTH_LONG).show();
            }
        }
    }

}
