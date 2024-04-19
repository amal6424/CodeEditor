package peepu.codeeditor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import peepu.codeeditor.databinding.ActivityProjectSettingsBinding;
import peepu.codeeditor.project.Project;
import peepu.codeeditor.project.ProjectSettings;

public class ProjectSettingsActivity extends AppCompatActivity {
    private ActivityProjectSettingsBinding binding;
    private Project project;
    private ProjectSettings settings;
    
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        project = new Project(getIntent().getExtras().get("project").toString());
        settings = new ProjectSettings(project);
        binding = ActivityProjectSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.projectSettingTextView.setText(settings.toString());
    }
    
}
