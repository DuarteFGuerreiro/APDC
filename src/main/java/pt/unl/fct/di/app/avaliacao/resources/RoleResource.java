package pt.unl.fct.di.app.avaliacao.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import java.util.logging.Logger;

import pt.unl.fct.di.app.avaliacao.util.RoleData;

@Path("/role")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RoleResource {
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doRole(RoleData data) {
		LOG.fine("Attempt to change role of user: " + data.getUsername() + " by admin " + data.getAdmin());

		Key adminKey = datastore.newKeyFactory().setKind("User").newKey(data.getAdmin());
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.getUsername());
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.getTokenID());
		Transaction txn = datastore.newTransaction();
		try {
			Entity user = txn.get(userKey);
			Entity token = txn.get(tokenKey);
			Entity admin = txn.get(adminKey);
			if (user == null) {
				LOG.warning("Failed to change role for username: " + data.getUsername());
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			if (token == null) {
				LOG.warning("Failed token change role for username: " + data.getUsername());
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			if (!token.getString("token_username").equals(data.getAdmin())) {
				LOG.warning("Failed change role for username: " + data.getUsername() + " wrong token for admin.");
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			if (token.getLong("token_expirationData") < System.currentTimeMillis()) {
				LOG.warning("Failed role change for username: " + data.getUsername() + " admin token expired.");
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			String adminRole = admin.getString("user_role");
			String userRole = user.getString("user_role");
			if (adminRole.equals("USER") || adminRole.equals("GBO") || data.getRole().equals("SU") || userRole.equals("SU")) {
				LOG.warning("Failed role change for username: " + data.getUsername() + " admin does not have permission.");
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();	
			}
			if (adminRole.equals("GA") && (data.getRole().equals("GA") || userRole.equals("GA"))) {
				LOG.warning("Failed role change for username: " + data.getUsername() + " admin is outranked.");
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();		
			}
			user = Entity.newBuilder(userKey).set("user_username", user.getString("user_username"))
					.set("user_password", user.getString("user_password"))
					.set("user_email", user.getString("user_email"))
					.set("user_role", data.getRole())
					.set("user_state", user.getString("user_state"))
					.set("user_address1", user.getString("user_address1"))
					.set("user_address2", user.getString("user_address2"))
					.set("user_phone", user.getString("user_phone"))
					.set("user_mobile", user.getString("user_mobile"))
					.set("user_locality", user.getString("user_locality"))
					.set("user_profile", user.getString("user_profile"))
					.set("removed", user.getString("removed"))
					.build();
			txn.put(user);
			LOG.info("User role changed " + data.getUsername());
			txn.commit();
			return Response.ok("{}").build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
}