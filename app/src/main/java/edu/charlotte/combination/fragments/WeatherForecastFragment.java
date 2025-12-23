package edu.charlotte.combination.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.charlotte.combination.R;
import edu.charlotte.combination.databinding.RowForecastBinding;
import edu.charlotte.combination.databinding.FragmentCurrentWeatherBinding;
import edu.charlotte.combination.databinding.FragmentWeatherForecastBinding;
import edu.charlotte.combination.models.DataService;
import edu.charlotte.combination.models.Forecast;
import edu.charlotte.combination.models.Weather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherForecastFragment extends Fragment {

    private static final String ARG_PARAM_CITY = "ARG_PARAM_CITY";
    private static final String TAG = "demo_ForecastWeatherFragment";
    private DataService.City mCity;
    ListView listView;

    public WeatherForecastFragment() {
        // Required empty public constructor
    }

    public static WeatherForecastFragment newInstance(DataService.City city) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCity = (DataService.City) getArguments().getSerializable(ARG_PARAM_CITY);
        }

    }
    FragmentWeatherForecastBinding binding;
    ForecastAdapter adapter;
    ArrayList<Forecast> forecasts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//
        binding = FragmentWeatherForecastBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Forecast");
//        binding.
        adapter = new ForecastAdapter(getActivity(), R.layout.row_forecast, forecasts);
        binding.listView.setAdapter(adapter);
        getForecast();
    }
    private final OkHttpClient client = new OkHttpClient();

    void getForecast() {

        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org/data/2.5/forecast").newBuilder()
                .addQueryParameter("appid", "fcdd9ae2f46fac511af274f8833ab8b3")
                .addQueryParameter("units", "imperial")
                .addQueryParameter("lat", String.valueOf(mCity.getLatitude()))
                .addQueryParameter("lon", String.valueOf(mCity.getLongitude()))
                .build();
        Log.d(TAG, "getForecast: " + mCity.getLatitude());
        Log.d(TAG, "getForecast: " + mCity.getLongitude());

//        Request request = new Request.Builder()
//                .url("https://www.theappsdr.com/clt.json")
//                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String body = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        JSONArray jsonList = jsonObject.getJSONArray("list");

                        forecasts.clear();

                        for (int i = 0; i < jsonList.length(); i++){
                            JSONObject forecoastItemObject = jsonList.getJSONObject(i);
                            Forecast forecast = new Forecast(forecoastItemObject);
                            forecasts.add(forecast);

                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    Log.d(TAG, "onResponse: " + response.code());
                    Log.d(TAG, "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: ");
                e.printStackTrace();
            }
        });
    }

    class ForecastAdapter extends ArrayAdapter<Forecast>{
        public ForecastAdapter(@NonNull Context context, int resource, @NonNull List<Forecast> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            RowForecastBinding mBinding;
            if (convertView == null) {
                mBinding = RowForecastBinding.inflate(getLayoutInflater(), parent, false);
                convertView = mBinding.getRoot();
                convertView.setTag(mBinding);
            } else {
                    mBinding = (RowForecastBinding) convertView.getTag();
            }

            Forecast forecast = getItem(position);

            mBinding.textViewTemp.setText(forecast.getTemp() + "F");
            mBinding.textViewDateTime.setText(forecast.getDt_txt());
            mBinding.textViewHumidity.setText("Humidity: " + forecast.getHumidity() + "%");
            mBinding.textViewDescription.setText("Description: " + forecast.getDescription());
            mBinding.textViewTempMax.setText("Max" + forecast.getTemp_max() + "F");
            mBinding.textViewTempMin.setText("Min" + forecast.getTemp_min() + "F");

            Picasso.get().load(forecast.getIconUrl()).into(mBinding.imageViewWeather);

            return convertView;
        }
    }

    FragmentWeatherForecastBinding mlistener;


}