package edu.charlotte.combination.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.charlotte.combination.databinding.FragmentHomeOptionsBinding;

public class HomeOptionsFragment extends Fragment {

    FragmentHomeOptionsBinding binding;
    HomeOptionsListener mListener;

    public HomeOptionsFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeOptionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        binding.buttonWeather.setOnClickListener(v -> mListener.openWeatherModule());
        binding.buttonTasks.setOnClickListener(v -> mListener.openTasksModule());
        binding.buttonForums.setOnClickListener(v -> mListener.openForumsModule());
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        try {
            mListener = (HomeOptionsListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + " must implement HomeOptionsListener");
        }
    }

    public interface HomeOptionsListener {
        void openWeatherModule();
        void openTasksModule();
        void openForumsModule();
    }
}
