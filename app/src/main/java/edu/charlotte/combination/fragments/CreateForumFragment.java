package edu.charlotte.combination.fragments;

import android.app.AlertDialog;
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

import java.util.Date;

import edu.charlotte.combination.databinding.FragmentCreateForumBinding;
import edu.charlotte.combination.models.Forum;

public class CreateForumFragment extends Fragment {

    public CreateForumFragment() {
        // Required empty public constructor
    }

    FragmentCreateForumBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateForumBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.onCancelSelection();
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String forumTitle = binding.editTextForumTitle.getText().toString();
                String forumText = binding.editTextForumText.getText().toString();
                if (forumTitle.isEmpty()){
                    Toast.makeText(getActivity(), "Please Enter Forum Title", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(getContext()).setMessage("Please Enter Forum Title").show();
                }else if (forumText.isEmpty()) {
                    Toast.makeText(getActivity(), "Please Enter Forum Description", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(getContext()).setMessage("Please Enter Forum Text").show();
                }
                else {
                    String userId = mAuth.getCurrentUser().getUid();
                    String forumDate = new Date().toString();
//                    Forum forum = new Forum( forumText, userId, forumDate, forumTitle, 0);
                    Forum forum =  new Forum();
                    forum.setForumCreatorUID(userId);
                    forum.setForumDate(forumDate);
                    forum.setForumText(forumText);
                    forum.setForumTitle(forumTitle);
                    forum.setForumLikesCount(0);
                    Log.d("demo_createForum", "onClick: create forum get current user display name:"+mAuth.getCurrentUser().getDisplayName());
                    forum.setForumCreatorName(mAuth.getCurrentUser().getDisplayName());

                    db.collection("forums")
                            .add(forum)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(getActivity(), "Forum Created", Toast.LENGTH_SHORT).show();
                                mlistener.onCreateForumSuccess(forum);
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Forum Creation Failed", Toast.LENGTH_SHORT).show();
                            });

                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mlistener = (CreateForumListener) getActivity();
    }

    CreateForumListener mlistener;

    public interface CreateForumListener {
        void onCancelSelection();
        void onCreateForumSuccess(Forum forum);

    }
}