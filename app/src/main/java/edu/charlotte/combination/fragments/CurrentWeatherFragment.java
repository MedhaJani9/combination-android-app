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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
//import edu.charlotte.combination.BuildConfig;

import edu.charlotte.combination.databinding.FragmentCurrentWeatherBinding;
import edu.charlotte.combination.models.DataService;
import edu.charlotte.combination.models.Weather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrentWeatherFragment extends Fragment {

    Weather weather;
    private static final String ARG_PARAM_CITY = "ARG_PARAM_CITY";
    private static final String TAG = "demo_CurrentWeatherFragment";
    private DataService.City mCity;
    FragmentCurrentWeatherBinding binding;

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    public static CurrentWeatherFragment newInstance(DataService.City city) {
        CurrentWeatherFragment fragment = new CurrentWeatherFragment();
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Current Weather");

        binding.textViewCityName.setText(mCity.toString());

        Log.d(TAG, "onViewCreated: city : " + mCity.getCity());

        getWeather();
        binding.buttonCheckForecaste.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mlistener.goToForecastFragment(mCity);
            }
        });
//        binding.
    }

    private final OkHttpClient client = new OkHttpClient();
    Weather mWeather;


    void getWeather() {

        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org/data/2.5/weather").newBuilder()
                .addQueryParameter("appid", "actual.API.key")
                .addQueryParameter("units", "imperial")
                .addQueryParameter("lat", String.valueOf(mCity.getLatitude()))
                .addQueryParameter("lon", String.valueOf(mCity.getLongitude()))
                .build();

        //make a secret key for api in .env file

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
                        mWeather = new Weather(jsonObject);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                binding.textViewtemp.setText(mWeather.getTemp() + "F");
                                binding.textViewTempMax.setText(mWeather.getTemp_max() + "F");
                                binding.textViewTempMin.setText(mWeather.getTemp_min() + "F");

                                binding.textViewDesc.setText(mWeather.getDescription());
                                binding.textViewHumidity.setText(mWeather.getHumidity() + "%");

                                binding.textViewWindSpeed.setText(mWeather.getSpeed() + "mph");
                                binding.textViewWindDeg.setText(mWeather.getDeg() + "deg");
                                binding.textViewCloudiness.setText(mWeather.getCloudiness()+ "%");

                                Picasso.get().load(mWeather.getIconUrl()).into(binding.imageViewIconWeather);

                            }
                        });



                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: ");
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mlistener = (CurrentWeatherFragmentListener) getActivity();
    }

    CurrentWeatherFragmentListener mlistener;

    public interface CurrentWeatherFragmentListener{
        void goToForecastFragment(DataService.City city);
    }
}