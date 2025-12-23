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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.charlotte.combination.R;
import edu.charlotte.combination.databinding.FragmentAddTaskBinding;
import edu.charlotte.combination.models.Priority;
import edu.charlotte.combination.models.Task;
import edu.charlotte.combination.models.Task;

public class AddTaskFragment extends Fragment {
    private Priority selectedPriority;
    private String selectedCategory;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public void setSelectedPriority(Priority selectedPriority) {
        this.selectedPriority = selectedPriority;
        Log.d("demo", "setSelectedPriority: " + selectedPriority);
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
        Log.d("demo", "setSelectedCategory: " + selectedCategory);
    }

    public AddTaskFragment() {
        // Required empty public constructor
    }

    FragmentAddTaskBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddTaskBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoSelectCategory();
            }
        });

        binding.buttonSelectPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoSelectPriority();
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancelSelection();
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.editTextName.getText().toString();
                Log.d("demo", "onClick: " + name);
                if(name.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Name !!", Toast.LENGTH_SHORT).show();
                } else if(selectedCategory == null) {
                    Toast.makeText(getActivity(), "Select Category !!", Toast.LENGTH_SHORT).show();
                } else if(selectedPriority == null) {
                    Toast.makeText(getActivity(), "Select Priority !!", Toast.LENGTH_SHORT).show();
                } else {
                    //Task task = new Task(name, selectedCategory, selectedPriority);
                    //mListener.onTaskAdded(task);
                    String userId = mAuth.getCurrentUser().getUid();
                    Task task = new Task(name, selectedCategory, selectedPriority, userId);
                    db.collection("tasks")
                            .add(task).addOnSuccessListener(documentReference -> {
                                Toast.makeText(getActivity(), "Task added successfully!", Toast.LENGTH_SHORT).show();
                                mListener.onTaskAdded(task);
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Error adding task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });
        UpdateUI();
    }

    private void UpdateUI(){
        if(selectedCategory != null) {
            binding.textViewCategory.setText(selectedCategory);
        } else {
            Log.d("demo", "UpdateUI: " + selectedCategory);
            binding.textViewCategory.setText("N/A");
        }

        if(selectedPriority != null) {
            binding.textViewPriority.setText(selectedPriority.getName());
            Log.d("demo", "UpdateUI: " + selectedPriority.getName());
        } else {
            binding.textViewPriority.setText("N/A");
            Log.d("demo", "UpdateUI: " + selectedPriority);
        }
    }

    AddTaskListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddTaskListener) {
            mListener = (AddTaskListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AddTaskListener");
        }
    }

    public interface AddTaskListener {
        void gotoSelectPriority();
        void gotoSelectCategory();
        void onTaskAdded(edu.charlotte.combination.models.Task task);
        void onCancelSelection();
    }
}