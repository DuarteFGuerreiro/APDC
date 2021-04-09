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

import pt.unl.fct.di.app.avaliacao.util.ModifyData;

@Path("/modify")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ModifyResource {
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doModification(ModifyData data) {
		LOG.fine("Attempt to modify user: " + data.getUsername());

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.getUsername());
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.getTokenID());
		Transaction txn = datastore.newTransaction();
		try {
			Entity user = txn.get(userKey);
			Entity token = txn.get(tokenKey);
			if (user == null) {
				LOG.warning("Failed modify for username: " + data.getUsername());
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			if(user.getString("user_state").equals("DISABLED")) {
				LOG.warning("Failed modify for username: " + data.getUsername() + " user was disabled.");
				return Response.status(Status.FORBIDDEN).build();	
			}
			if (token == null) {
				LOG.warning("Failed token modify for username: " + data.getUsername());
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			if (!token.getString("token_username").equals(data.getUsername())) {
				LOG.warning("Failed modify for username: " + data.getUsername() + " wrong username for this token.");
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			if (token.getLong("token_expirationData") < System.currentTimeMillis()) {
				LOG.warning("Failed modify for username: " + data.getUsername() + " token expired.");
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			String password = data.getPassword();
			String email = data.getEmail();
			String address1 = data.getAddress1();
			String address2 = data.getAddress2();
			String phone = data.getPhone();
			String mobile = data.getMobile();
			String locality = data.getLocality();
			String profile = data.getProfile();
			if (password == null)
				password = user.getString("user_password");
			
			if (email == null)
				email = user.getString("user_email");
			
			if (address1 == null)
				address1 = user.getString("user_address1");

			if (address2 == null)
				address2 = user.getString("user_address2");

			if (phone == null)
				phone = user.getString("user_phone");

			if (mobile == null)
				mobile = user.getString("user_mobile");

			if (locality == null)
				locality = user.getString("user_locality");

			if (profile == null)
				profile = user.getString("user_profile");

			user = Entity.newBuilder(userKey).set("user_username", user.getString("user_username"))
					.set("user_password", password)
					.set("user_email", email)
					.set("user_role", user.getString("user_role"))
					.set("user_state", user.getString("user_state"))
					.set("user_address1", address1)
					.set("user_address2", address2)
					.set("user_phone", phone)
					.set("user_mobile", mobile)
					.set("user_locality", locality)
					.set("user_profile", profile)
					.set("removed", user.getString("removed"))
					.build();
			txn.put(user);
			LOG.info("User modified " + data.getUsername());
			txn.commit();
			return Response.ok("{}").build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
}