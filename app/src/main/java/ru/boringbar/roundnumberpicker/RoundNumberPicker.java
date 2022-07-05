package ru.boringbar.roundnumberpicker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

public class RoundNumberPicker extends RelativeLayout {

    ArrayList<TextView> numbers = new ArrayList<>();
    View cursor;

    private float parentRadius;
    private float parentDiameter;

    private int parentWidth;
    private int parentHeight;

    private float offsetAngle = -90F;
    private double scale = 0.8;

    private int startPosition;
    private int itemCount = 12;

    int waitAllItems = 0;

    @Nullable
    private OnNumberChangeListener onHourChangeListener;

    public RoundNumberPicker(Context context) {
        super(context);
        init();
    }

    public RoundNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RoundNumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(){
        //Ждем прорисовки view для подсчета размеров и возможности добавить числа
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                parentWidth = getWidth();
                parentHeight = getHeight();

                parentDiameter = parentHeight;
                parentRadius = parentDiameter * 0.5F;

                addNumbers();
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    int selectedHour = 0;
    boolean moved = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        var x = event.getX();// - parentRadius;
        var y = event.getY();//- parentRadius;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moved = false;
                //Log.i("Positioning", "On action DOWN: " + moved);
                break;

            case MotionEvent.ACTION_MOVE:
                moved = true;
                selectedHour = onFaceClockTouch(x, y);
                break;

            case MotionEvent.ACTION_UP:
                if (moved) {
                    if (onHourChangeListener != null)
                        onHourChangeListener.onChange(selectedHour + startPosition);
                } else {
                    //Log.i("Positioning", "On action UP. Not moved");
                    selectedHour = onFaceClockTouch(x, y);
                    if (onHourChangeListener != null)
                        onHourChangeListener.onChange(selectedHour + startPosition);
                }

                setCursor(selectedHour);
                moved = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * При касании циферблата
     * @param x Касание от левого верхнего угла по x
     * @param y Касание от левого верхнего угла по y
     * @return положение в массиве чисел
     */
    private int onFaceClockTouch(float x, float y){
        //Поиск угла нажатия
        var angle = Math.toDegrees(Math.atan2(x - parentRadius,
                parentRadius - y) + 360.0) % 360.0;

        //Смещение угла
        var biasAngle = angle + offsetAngle;
        angle = biasAngle > 360 ? biasAngle - 360 :
                biasAngle < 0 ? biasAngle + 360 : biasAngle;

        //Поиск шага
        var hourSteep = 360 / itemCount;
        var item = angle / hourSteep;

        return (int)item;
    }

    /**
     * Получить расположение для курсора
     * @param textView привязка к числу
     * @return расположение для курсора
     */
    private LayoutParams getCursorLayoutParams(TextView textView){
        LayoutParams textLayoutParams = (LayoutParams) textView.getLayoutParams();

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        int width =  dpToPx(40);
        int height =  dpToPx(40);
        layoutParams.width = width;
        layoutParams.height = height;

//            Log.i("CursorSet", "tW: " + textViewWidth + " tH: " + textViewHeight);
//            Log.i("CursorSet", "topMargin: " + textLayoutParams.topMargin + " lefMargin: " + textLayoutParams.leftMargin);
        return layoutParams;
    }

    /**
     * Установить слушателя при выборе числа на окружности
     */
    public void setOnNumberChangeListener(@Nullable OnNumberChangeListener onHourChangeListener) {
        this.onHourChangeListener = onHourChangeListener;
    }

    /**
     * Удаляет выделяющий выбор курсор
     */
    public void removeCursor(){
        Log.i("Positioning","removeCursor");
        this.removeView(cursor);
        cursor = null;
    }

    /**
     * Изменить кол-во выводимых чисел
     * @param itemCount по умолчанию 12
     */
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    /**
     * Поворот чисел на окружности
     * @param offsetAngle в градусах
     */
    public void setOffsetAngle(float offsetAngle) {
        this.offsetAngle = offsetAngle;
    }

    /**
     * Масштабирование положения чисел относительно окружности
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * Смещение в числах для отображения
     * @param startPosition по умолчанию 0
     */
    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    /**
     * Добавляет числа на окружность
     */
    private void addNumbers(){
        for(int i = 0; i < itemCount; i++){
            TextView textView = new TextView(getContext());
            textView.setId(View.generateViewId());
            textView.setText(String.valueOf(startPosition + i));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextColor(ResourcesCompat.getColor(getResources(),
                    R.color.black, getContext().getTheme()));
            textView.setElevation(1.1F);

            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            var position = getPosition(i);
            layoutParams.leftMargin = position.x;
            layoutParams.topMargin = position.y;

            addView(textView, layoutParams);
            textView.post(new Runnable() {
                @Override
                public void run() {

                    layoutParams.leftMargin -= (textView.getWidth() /2);
                    layoutParams.topMargin -= (textView.getHeight() /2);

                    if(waitAllItems == itemCount - 1){
                        requestLayout();
                        waitAllItems = 0;
                    }

                    waitAllItems++;
                }
            });
            numbers.add(textView);
        }
    }

    /**
     * Удаляет и заново добавляет числа на окружность
     */
    private void refresh(){
        removeAllViews();
        numbers.clear();
        addNumbers();
        /*for(int i = 0; i < numbers.size(); i++){
            TextView textView =  numbers.get(i);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
            var newPos = getPosition(i);
            layoutParams.leftMargin = (int)newPos.x;
            layoutParams.topMargin = (int)newPos.y;
            textView.post(new Runnable() {
                @Override
                public void run() {
                    layoutParams.leftMargin -= (int) (((double)textView.getWidth() * scale) * 0.5D);
                    layoutParams.topMargin -= (int) (((double)textView.getHeight() * scale) * 0.5D);

                    if(waitAllItems == itemCount - 1){
                        requestLayout();
                        waitAllItems = 0;
                    }

                    waitAllItems++;
                }
            });
        }*/
    }

    /**
     * Установить положение выделения
     * @param position порядковый номер в массиве чисел
     */
    private void setCursor(int position){

        TextView textView = numbers.get(position);

        if(cursor == null){
            cursor = new View(getContext());
            cursor.setElevation(1F);
            cursor.setId(View.generateViewId());
            cursor.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.selected_dot));

            LayoutParams layoutParams = getCursorLayoutParams(textView);
            addView(cursor, layoutParams);
            requestLayout();
        }

        LayoutParams textLayoutParams = (LayoutParams) textView.getLayoutParams();
        LayoutParams viewLayoutParams = (LayoutParams) cursor.getLayoutParams();

        int width = viewLayoutParams.width;
        int height = viewLayoutParams.height;
        int textViewWidth = textView.getWidth();
        int textViewHeight = textView.getHeight();

        int x = textLayoutParams.leftMargin - (width >> 1) + (textViewWidth >> 1);
        int y = textLayoutParams.topMargin  - (height >> 1) + (textViewHeight >> 1);

        cursor.animate().translationX(x).translationY(y).start();
    }

    /**
     * Получить координаты точки на окружности
     * @param position порядковый номер в массиве чисел
     * @return Координаты точки по x и y
     */
    private Point getPosition(int position){
        //a = c ⋅ cos(β)
        //b = c ⋅ cos(α)
        var angle = (360 / itemCount) * position + offsetAngle;

        var scaledRadius = parentRadius * scale;
        var radiusDiff = parentRadius - scaledRadius;
        var transformX = (scaledRadius + (scaledRadius * Math.cos(Math.toRadians(angle)))) + radiusDiff;
        var transformY = (scaledRadius + (scaledRadius * Math.sin(Math.toRadians(angle)))) + radiusDiff;

        Point result = new Point();
        result.x = (int) transformX;
        result.y = (int) transformY;

        return result;
    }

    /**
     * Расует круг
     * @param diameter диаметр
     * @param color цвет
     * @param fill заполнен цветом или линия
     * @return круг
     */
    public Drawable getCircle(float diameter, int color, boolean fill) {
        OvalShape ovalShape = new OvalShape();
        ovalShape.resize(diameter, diameter);

        ShapeDrawable shapeDrawable = new ShapeDrawable(ovalShape);
        shapeDrawable.getPaint().setColor(color);
        shapeDrawable.getPaint().setStyle(fill ? Paint.Style.FILL : Paint.Style.STROKE);
        if(!fill)
            shapeDrawable.getPaint().setStrokeWidth(20);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
        return shapeDrawable;
    }

    /**
     * Перевод dp в пиксели
     * @param dp размер в dp
     * @return размер в px
     */
    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Слушатель выбора числа на окружности
     */
    public interface OnNumberChangeListener {
        void onChange(int hour);
    }
}
