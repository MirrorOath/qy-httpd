package Fun;

import java.util.Date;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 公有方法类
 * 
 * @author Administrator
 *
 */
public class UtilsPuMonth {
    public static String Q(String s) {
        return "'" + s + "'";
    }

    /**
     * 保存及修改信息的公共方法
     * 
     * @param db
     * @param reginfo
     * @param czlist
     * @param qpczlist
     * @param zcdm
     * @param optype
     * @param times
     * @return
     */
    public static boolean savedata1(String db, String reginfo, String czlist, String qpczlist, String zcdm,
            String optype, String times) {
        String sql = "select * from " + reginfo + " where (zcdm=" + Q(zcdm) + " or zcdmtsg08=" + Q(zcdm) + ")";
        Record re = Db.use(db).findFirst(sql);
        int typcode = 1;
        try {
            typcode = Integer.valueOf(optype);

        } catch (Exception e) {
            // TODO: handle exception
            typcode = 1;
        }
        if (re != null) {
            if ((re.getDate("LastXjrq_Jyjl").getTime() < new Date().getTime())
                    || (re.getDate("LastBFRQ_Jyjl").getTime() < new Date().getTime())) {
                sql = "insert into " + czlist
                        + " (checkdatetime,rno,Syzbh,zcdm,Pcode,Type_name,Medium_name,Pid,Pdate,LastJydate,"
                        + "LastXjrq_Jyjl,LastBFRQ_Jyjl,Safedate,InfoLabeltype,InfoLabel,Checkresult,updatetime,OpType) "
                        + "select now(),RNO,Syzbh,zcdm,Pcode,Type_name,Medium_name,Pid,Pdate,LastJydate,LastXjrq_Jyjl,"
                        + "LastBFRQ_Jyjl,BFRQ,InfoLabeltype,InfoLabeltype_name,'1',now()," + typcode + " from "
                        + reginfo + " where zcdm=" + Q(re.getStr("zcdm"));

            } else {
                sql = "insert into " + czlist
                        + " (checkdatetime,rno,Syzbh,zcdm,Pcode,Type_name,Medium_name,Pid,Pdate,LastJydate,"
                        + "LastXjrq_Jyjl,LastBFRQ_Jyjl,Safedate,InfoLabeltype,InfoLabel,Checkresult,updatetime,OpType)"
                        + " select now(),RNO,Syzbh,zcdm,Pcode,Type_name,Medium_name,Pid,Pdate,LastJydate,LastXjrq_Jyjl,"
                        + "LastBFRQ_Jyjl,BFRQ,InfoLabeltype,InfoLabeltype_name,'0',now()," + typcode + " from "
                        + reginfo + " where zcdm=" + Q(re.getStr("zcdm"));

            }
            String upsql = "select * from " + qpczlist + " where RNOID=" + Q(re.getStr("RNO"));
            Record rs = Db.use(db).findFirst(upsql);
            if (optype.equals("czjc")) {// 充装检查
                upsql = "update " + reginfo + " set lczdw=" + Q(rs.get("Rname").toString()) + ",lczrq=" + Q(times) + ","
                        + "lastczdz=" + Q(rs.get("RAddress").toString()) + " where zcdm=" + Q(re.getStr("zcdm"));
            }
            if (optype.equals("czqjc") || optype.equals("1")) {// 充装前检查
                upsql = "update " + reginfo + " set lczdw=" + Q(rs.get("Rname").toString()) + ",lczjcrq=" + Q(times)
                        + ",lastczdz=" + Q(rs.get("RAddress").toString()) + " where zcdm=" + Q(re.getStr("zcdm"));
            }
            if (optype.equals("czfj") || optype.equals("2")) {// 充装后复检
                upsql = "update " + reginfo + " set lczdw=" + Q(rs.get("Rname").toString()) + ",lczfjrq=" + Q(times)
                        + ",lastczdz=" + Q(rs.get("RAddress").toString()) + " where zcdm=" + Q(re.getStr("zcdm"));
            }
            if (optype.equals("2")) {

            }
            if (Db.use(db).update(sql) > 0 && Db.use(db).update(upsql) > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
