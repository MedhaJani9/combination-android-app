package edu.charlotte.combination.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import edu.charlotte.combination.R;
import edu.charlotte.combination.databinding.FragmentTasksBinding;
import edu.charlotte.combination.databinding.TaskListItemBinding;
import edu.charlotte.combination.models.Task;

public class TasksFragment extends Fragment {
    public TasksFragment() {
        // Required empty public constructor
    }

    FragmentTasksBinding binding;
    TasksAdapter adapter;
    ArrayList<Task> taskList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoAddTask();
            }
        });

        adapter = new TasksAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        setUpTasksListener();
    }

    private void setUpTasksListener() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        listenerRegistration = db.collection("tasks")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    taskList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Task task = doc.toObject(Task.class);
                            task.setTaskId(doc.getId());
                            taskList.add(task);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });

    }

    class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {
        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TaskListItemBinding itemBinding = TaskListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new TaskViewHolder(itemBinding);
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Task task = taskList.get(position);
            holder.bind(task);
        }

        class TaskViewHolder extends RecyclerView.ViewHolder {
            TaskListItemBinding itemBinding;
            Task currentTask;

            public TaskViewHolder(TaskListItemBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;
            }

            public void bind(Task task) {
                currentTask = task;
                itemBinding.textViewName.setText(task.getName());
                itemBinding.textViewCategory.setText(task.getCategory());
//                itemBinding.textViewPriority.setText(task.getPriority().getName());

                itemBinding.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //delete task from firestore
                        db.collection("tasks")
                                .document(currentTask.getTaskId())
                                .delete();
                        //The listener automatically updates the list
                    }
                });
            }
        }
    }


    TasksListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TasksListener) {
            mListener = (TasksListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement TasksListener");
        }
    }

    public interface TasksListener {
        void gotoAddTask();
    }
}
