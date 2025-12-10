package uqac.dim.transportbus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Utilisation du bon layout pour chaque item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Affectation des données aux vues de chaque item
        User user = userList.get(position);
        holder.usernameTextView.setText(user.getUsername());  // Utilisation de getUsername()
        holder.roleTextView.setText(user.getRole());
    }

    @Override
    public int getItemCount() {
        return userList.size(); // Nombre d'éléments dans la liste
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView, roleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialisation des vues des items
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
        }
    }
}
