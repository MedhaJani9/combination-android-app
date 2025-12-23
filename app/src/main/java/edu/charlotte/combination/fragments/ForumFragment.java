package edu.charlotte.combination.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

import edu.charlotte.combination.databinding.CommentRowItemBinding;
import edu.charlotte.combination.databinding.FragmentForumBinding;
import edu.charlotte.combination.models.Comment;

public class ForumFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ArrayList<Comment> commentList = new ArrayList<>();
    CommentsAdapter adapter;
    ListenerRegistration commentListener;
    CommentRowItemBinding itemBinding;
    private static final String ARG_FORUM_ID = "forum_id";
    private String forumId;

    public ForumFragment() {
        // Required empty public constructor
    }

    public static ForumFragment newInstance(String forumId) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_FORUM_ID, forumId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            forumId = getArguments().getString(ARG_FORUM_ID);
        }
    }

    FragmentForumBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForumBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommentsAdapter();
        binding.recyclerViewComments.setAdapter(adapter);

        loadForumDetails();
        setupCommentsListener();

        binding.buttonSubmitComment.setOnClickListener(v -> postComment());

    }

    private void loadForumDetails() {
        Log.d("demo", "loadForumDetails: " + forumId);
        db.collection("forums").document(forumId).get()
                .addOnSuccessListener(doc -> {
                    if(doc.exists()) {
                        binding.textViewForumTitle.setText(doc.getString("forumTitle"));
                        binding.textViewForumText.setText(doc.getString("forumText"));
                        binding.textViewForumCreatedBy.setText(doc.getString("forumCreatedBy"));
//                        binding.textViewCommentsCount.setText(doc.getString("commentsCount"));

                        Long count = doc.getLong("commentsCount");
                        if (count == null) count = 0L;
                        binding.textViewCommentsCount.setText(count + " Comments");
                    }
                });


    }

    //Ascending thing is interesting
    private void setupCommentsListener() {
        commentListener = db.collection("forums")
                .document(forumId)
                .collection("comments")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    commentList.clear();
                    if (value != null) {
//                        commentList.addAll(value.toObjects(Comment.class));
                        for (QueryDocumentSnapshot doc : value) {
                            Comment c = doc.toObject(Comment.class);
                            c.setCommentId(doc.getId()); // map Firestore ID
                            commentList.add(c);
                        }
                    }
                    binding.textViewCommentsCount
                            .setText(commentList.size() + " Comments");
                    adapter.notifyDataSetChanged();
                });
    }

    private void postComment() {
        String text = binding.editTextComment.getText().toString().trim();
        //check if edittext is empty or not
        if(text.isEmpty()) return;

        Comment comment = new Comment(
                text,
                mAuth.getCurrentUser().getUid(),
                mAuth.getCurrentUser().getDisplayName(),
                new Date()
        );

        //comments as a sub collection of forums collection
        DocumentReference commentRef = db.collection("forums")
                .document(forumId)
                .collection("comments")
                .document();

        comment.setCommentId(commentRef.getId());

        commentRef.set(comment)
                .addOnSuccessListener(unused -> {
                    binding.editTextComment.setText("");
                    db.collection("forums")
                            .document(forumId)
                            .update("commentsCount", FieldValue.increment(1));
                });
//                .add(comment)
//                .addOnSuccessListener(docRef -> {
//                    comment.setCommentId(docRef.getId());
//                    docRef.update("commentId", docRef.getId());
//                    binding.editTextComment.setText("");
//                });

    }

//    private void incrementCount() {
//        db.collection("forums")
//                .document(forumId)
//                .update("commentsCount", FieldValue.increment(1));
//    }

    class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CommentRowItemBinding itemBinding =
                    CommentRowItemBinding.inflate(getLayoutInflater(), parent, false);
            return new CommentViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
            holder.bind(commentList.get(position));
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        class CommentViewHolder extends RecyclerView.ViewHolder {
            Comment comment;

            public CommentViewHolder(CommentRowItemBinding binding) {
                super(binding.getRoot());
                itemBinding = binding;
            }

            public void bind(Comment comment) {
                this.comment = comment;

                itemBinding.textViewCommentCreatedBy.setText(comment.getOwnerName());
                itemBinding.textViewCommentText.setText(comment.getText());
                itemBinding.textViewCommentCreatedAt.setText(comment.getCreatedAt().toString());

                // Delete button only visible to the owner of the comment
                if(comment.getOwnerId().equals(mAuth.getCurrentUser().getUid())) {
                    itemBinding.imageViewDelete.setVisibility(View.VISIBLE);
                    itemBinding.imageViewDelete.setOnClickListener(v ->
                            deleteComment()
                    );
                } else {
                    itemBinding.imageViewDelete.setVisibility(View.GONE);
                }
            }

            void deleteComment() {
                db.collection("forums")
                        .document(forumId)
                        .collection("comments")
                        .document(comment.getCommentId())
                        .delete()
                        .addOnSuccessListener(unused -> {
                            db.collection("forums")
                                    .document(forumId)
                                    .update("commentsCount", FieldValue.increment(-1));
                        });
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mlistener = (ForumFragmentListener) getActivity();
    }

    ForumFragmentListener mlistener;

    public interface ForumFragmentListener{

    }

}