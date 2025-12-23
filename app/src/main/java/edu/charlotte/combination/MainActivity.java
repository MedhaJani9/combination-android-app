package edu.charlotte.combination;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import edu.charlotte.combination.fragments.AddTaskFragment;
import edu.charlotte.combination.fragments.CurrentWeatherFragment;
import edu.charlotte.combination.fragments.HomeOptionsFragment;
import edu.charlotte.combination.fragments.LoginFragment;
//import edu.charlotte.combination.fragments.TasksFragment;
import edu.charlotte.combination.fragments.ForumsFragment;
import edu.charlotte.combination.fragments.CreateForumFragment;
import edu.charlotte.combination.fragments.ForumFragment;
import edu.charlotte.combination.fragments.CitiesFragment;
import edu.charlotte.combination.fragments.SelectCategoryFragment;
import edu.charlotte.combination.fragments.SelectPriorityFragment;
import edu.charlotte.combination.fragments.SignUpFragment;
import edu.charlotte.combination.fragments.TasksFragment;
import edu.charlotte.combination.fragments.WeatherForecastFragment;
import edu.charlotte.combination.models.DataService;
import edu.charlotte.combination.models.Forum;
import edu.charlotte.combination.models.Priority;
import edu.charlotte.combination.models.Task;

public class MainActivity extends AppCompatActivity implements
        LoginFragment.LoginListener,
        HomeOptionsFragment.HomeOptionsListener,
        ForumsFragment.ForumsListener,
        CreateForumFragment.CreateForumListener,
        ForumFragment.ForumFragmentListener,
        SignUpFragment.SignUpListener,
        TasksFragment.TasksListener,
        CitiesFragment.CitiesFragmentListener,
        AddTaskFragment.AddTaskListener,
        SelectCategoryFragment.SelectCategoryListener,
        SelectPriorityFragment.SelectPriorityListener,
        CurrentWeatherFragment.CurrentWeatherFragmentListener
        // placeholder for weather & tasks fragments' listeners (will be added later)
         {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // start with LoginFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, new LoginFragment())
                .commit();
    }

             @Override
             public void createNewAccount() {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new SignUpFragment())
                        .commit();
             }

             /* LoginListener */
    @Override
    public void loginSuccess() {
        // after login, show Home options
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, new edu.charlotte.combination.fragments.HomeOptionsFragment())
                .addToBackStack(null)
                .commit();
    }


    /* HomeOptionsListener */


             @Override
             public void createForum() {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new CreateForumFragment())
                        .addToBackStack(null)
                        .commit();
             }

             @Override
             public void openForumDetails(String forumId) {
                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.main, ForumFragment.newInstance(forumId))
                         .addToBackStack(null)
                         .commit();
             }

             @Override
             public void logoutUser() {
                 FirebaseAuth.getInstance().signOut();
                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.main, new LoginFragment())
                         .commit();
             }


             @Override
             public void gotoSelectPriority() {
                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.main, new SelectPriorityFragment())
                         .addToBackStack(null)
                         .commit();
             }

             @Override
             public void gotoSelectCategory() {
                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.main, new SelectCategoryFragment())
                         .addToBackStack(null)
                         .commit();
             }

             @Override
             public void onTaskAdded(Task task) {
                getSupportFragmentManager().popBackStack();
             }

             @Override
             public void onCategorySelected(String category) {
                 Log.d("demo", "onCategorySelected: " + category);
                AddTaskFragment addTaskFragment = (AddTaskFragment) getSupportFragmentManager().findFragmentByTag("AddTaskFragment");
                if (addTaskFragment != null) {
                    addTaskFragment.setSelectedCategory(category);
                }
                getSupportFragmentManager().popBackStack();

             }

             @Override
             public void onPrioritySelected(Priority priority) {
                 Log.d("demo", "onPrioritySelected: " + priority);
                 AddTaskFragment addTaskFragment = (AddTaskFragment) getSupportFragmentManager().findFragmentByTag("AddTaskFragment");
                 if (addTaskFragment != null) {
                     addTaskFragment.setSelectedPriority(priority);
                 }
                 getSupportFragmentManager().popBackStack();
             }

             /* CreateForumListener */
    @Override
    public void onCancelSelection() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onCreateForumSuccess(Forum forum) {
        getSupportFragmentManager().popBackStack();
    }

             @Override
             public void openWeatherModule() {
                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.main, new CitiesFragment())
                         .addToBackStack(null)
                         .commit();
             }

             @Override
             public void openTasksModule() {
                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.main, new TasksFragment())
                         .addToBackStack(null)
                         .commit();
             }

             @Override
             public void openForumsModule() {
                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.main, new ForumsFragment())
                         .addToBackStack(null)
                         .commit();
             }

             @Override
             public void cancelCreateAccount() {
                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.main, new LoginFragment())
                         .commit();

             }

             @Override
             public void onCreateAccountSuccess() {
                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.main, new HomeOptionsFragment())
                         .commit();
             }

             @Override
             public void gotoAddTask() {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new AddTaskFragment(), "AddTaskFragment")
                        .addToBackStack(null)
                        .commit();
             }

             @Override
             public void gotoCurrentWeather(DataService.City city) {
                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.main, CurrentWeatherFragment.newInstance(city))
                         .addToBackStack(null)
                         .commit();
             }

             @Override
             public void goToForecastFragment(DataService.City city) {
                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.main, WeatherForecastFragment.newInstance(city))
                         .addToBackStack(null)
                         .commit();
             }
         }
