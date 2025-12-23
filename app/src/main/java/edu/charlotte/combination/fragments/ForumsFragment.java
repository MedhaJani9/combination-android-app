package edu.charlotte.combination.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import edu.charlotte.combination.R;
import edu.charlotte.combination.databinding.ForumRowItemBinding;
import edu.charlotte.combination.databinding.FragmentForumsBinding;
import edu.charlotte.combination.models.Forum;

//Assignment-15
//Group-6
//Medha Jani and Ryan Chen

public class ForumsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<Forum> forumList = new ArrayList<>();
    private ForumsAdapter adapter;
    private ListenerRegistration listenerRegistration;

    private FragmentForumsBinding binding;
    private ForumsListener mListener;

    public ForumsFragment() {}

    public static ForumsFragment newInstance() {
        return new ForumsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForumsBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ForumsAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logoutUser();
            }
        });

        binding.buttonCreateForum.setOnClickListener(v -> mListener.createForum());
        binding.buttonLogout.setOnClickListener(v -> mListener.logoutUser());

        loadForums();
    }

    private void loadForums() {
        listenerRegistration = db.collection("forums")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    forumList.clear();

                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Forum forum = doc.toObject(Forum.class);
                            forum.setForumId(doc.getId());
                            //I chnaged names midway, so normalizing them, somewhere it is title, or forumTitle, mainly it is because of forumText and description
                            //I know it was simple but I made it complicated, but i did not know
                            // normalize old/new field names
                            if (forum.getForumTitle() == null) {
                                forum.setForumTitle(firstNonNull(
                                        doc.getString("title"),
                                        doc.getString("ForumTitle")
                                ));
                            }
                            if (forum.getForumText() == null) {
                                forum.setForumText(firstNonNull(
                                        doc.getString("description"),
                                        doc.getString("ForumText")
                                ));
                            }
                            if (forum.getForumCreatorName() == null) {
                                forum.setForumCreatorName(firstNonNull(
                                        doc.getString("ownerName"),
                                        doc.getString("ForumCreatorName")
                                ));
                            }
                            if (forum.getForumCreatorUID() == null) {
                                forum.setForumCreatorUID(firstNonNull(
                                        doc.getString("ownerId"),
                                        doc.getString("ForumCreatorUID")
                                ));
                            }
                            forumList.add(forum);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private String firstNonNull(String... vals) {
        for (String s : vals) if (s != null) return s;
        return "";
    }

    class ForumsAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<ForumsAdapter.ForumsViewHolder> {

        @NonNull
        @Override
        public ForumsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ForumRowItemBinding itemBinding = ForumRowItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ForumsViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ForumsViewHolder holder, int position) {
            holder.bind(forumList.get(position));
        }

        @Override
        public int getItemCount() { return forumList.size(); }

        class ForumsViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            ForumRowItemBinding itemBinding;
            Forum currentForum;

            public ForumsViewHolder(@NonNull ForumRowItemBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;
            }

            void bind(Forum forum) {
                currentForum = forum;
                itemBinding.textViewForumTitle.setText(forum.getForumTitle());
                itemBinding.textViewForumText.setText(forum.getForumText());
                itemBinding.textViewForumCreatedBy.setText(forum.getForumCreatorName());
                itemBinding.textViewForumDate.setText(forum.getForumDate());

                // go to the forum details page(ForumFragment)
                itemView.setOnClickListener(v -> mListener.openForumDetails(forum.getForumId()));

                // Delete button only vsible to the owner
                if (forum.getForumCreatorUID().equals(mAuth.getUid())) {
                    itemBinding.imageViewDelete.setVisibility(View.VISIBLE);
                    itemBinding.imageViewDelete.setOnClickListener(v -> {
                        db.collection("forums").document(forum.getForumId()).delete();
                        db.collection("forums")
                                .document(forum.getForumId())
                                .collection("comments")
                                .addSnapshotListener((value, error) -> {
                                    if (error != null) return;

                                    if (value != null) {
                                        for (QueryDocumentSnapshot doc : value) {
                                            doc.getReference().delete();
                                        }
                                    }
                                });
                    });
                } else itemBinding.imageViewDelete.setVisibility(View.GONE);

                // likes are stored as a  sub collection of forums collection, also checks if the user has liked the forum
                //creating a boolean value and checking if liked or not

                //likes are stored as an array under the forums collection
                itemBinding.textViewForumLikesCount.setText("0 Likes");
                db.collection("forums")
                        .document(forum.getForumId())
                        .addSnapshotListener((value, error) -> {
                            if (value != null && value.exists()) {
                                ArrayList<String> likes = (ArrayList<String>) value.get("likes");
                                int count = (likes != null) ? likes.size() : 0;

                                itemBinding.textViewForumLikesCount.setText(count + " Likes");

                                boolean liked = likes != null && likes.contains(mAuth.getUid());

//                                for (DocumentSnapshot doc : value.getDocuments()) {
//                                    if (doc.getId().equals(mAuth.getUid())) {
//                                        liked = true;
//                                        break;
//                                    }
//                                }
                                //if liked filed heart and vice versa
                                itemBinding.imageViewLike.setImageResource(liked ?
                                        R.drawable.like_favorite : R.drawable.like_not_favorite);
                            }
                        });

                itemBinding.imageViewLike.setOnClickListener(v ->
                        toggleLike(forum.getForumId())
                );
            }

            void toggleLike(String forumId) {
                String userId = mAuth.getUid();
                Log.d("demo", "toggleLike: " + forumId);
                DocumentReference forumRef = db.collection("forums").document(forumId);

                forumRef.get().addOnSuccessListener(doc ->{
                    if (doc.exists()){

                        ArrayList<String> likes = (ArrayList<String>) doc.get("likes");

                        if (likes != null && likes.contains(userId)){
                            forumRef.update("likes", FieldValue.arrayRemove(userId));
                        }else {
                            forumRef.update("likes", FieldValue.arrayUnion(userId));
                        }
                    }else {
                        Log.d("demo", "toggleLike: ");
                    }

                });

//                db.collection("forums")
//                        .document(forumId)
//                        .collection("likes")
//                        .document(userId)
//                        .get().addOnSuccessListener(doc -> {
//                            if (doc.exists()) doc.getReference().delete();
//                            else doc.getReference().set(new LikeModel(userId));
//                        });
            }
        }
    }

    //Just a class to store the like data, to get the user Id of the user who liked the forum

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ForumsListener) getActivity();
    }

    public interface ForumsListener {
        void createForum();
        void openForumDetails(String forumId);
        void logoutUser();
    }
}
