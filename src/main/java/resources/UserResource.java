package resources;

import database.DB;
import org.apache.commons.lang3.time.DateUtils;
import types.User;

import javax.swing.table.DefaultTableModel;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.Date;

@Path("/User")
public class UserResource {

    private final String template;
    private final String defaultName;

    public UserResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
    }

    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login")
    public User getByLogin(@FormParam("userName")String userName, @FormParam("password")String password) {
        System.out.println(userName);
        System.out.println(password);


        Object oUserID = DB.getInstance().scalarSelect("SELECT userID FROM user WHERE userName = '"+userName+"' LIMIT 1");
        if(oUserID == null)
            return null;

        long userID = Long.valueOf((int) oUserID);

        Date dt = new Date();
        Date dtEnd = DateUtils.addMinutes(dt,10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(dt);
        String currentEndTime = sdf.format(dtEnd);

        String sql = "SELECT sessionID FROM session WHERE endTime > '"+currentTime+"' AND userID = "+String.valueOf(userID)+" LIMIT 1";
        Object oSessionID =  DB.getInstance().scalarSelect(sql);

        if(oSessionID == null){

            sql = "INSERT INTO session SELECT NULL, '"+currentTime+"', '"+currentEndTime+"',"+String.valueOf(userID);
            DB.getInstance().tableInsert(sql);

            sql = "SELECT sessionID FROM session WHERE endTime > '"+currentTime+"' AND userID = "+String.valueOf(userID)+" LIMIT 1";
            oSessionID =  DB.getInstance().scalarSelect(sql);
        }

        int sessionID = (int) oSessionID;

        sql = "SELECT userID,sessionID FROM session WHERE sessionID = "+sessionID+" LIMIT 1";
        DefaultTableModel tbl = DB.getInstance().tableSelect(sql);



        return new User(Long.valueOf((int)tbl.getValueAt(0, 0))
                , Long.valueOf((int)tbl.getValueAt(0, 1)));
    }
}
