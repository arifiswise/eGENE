package edu.ntu.eee.csn.ism.egene.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.ntu.eee.csn.ism.egene.exception.DBException;
import edu.ntu.eee.csn.ism.egene.exception.EgeneWebException;
import edu.ntu.eee.csn.ism.egene.util.Constant;
import edu.ntu.eee.csn.ism.egene.util.DBUtil;
import edu.ntu.eee.csn.ism.egene.util.ServletResponse;

@WebServlet(description = "Get data reference of eGENE", urlPatterns = { "/SvGetDataReference" })
public class SvGetDataReference extends HttpServlet {

	private static final long serialVersionUID = -4578875876383297688L;
	private static Logger LOGGER = Logger.getLogger(SvGetDataReference.class);

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("doGet:started");
			LOGGER.debug("params=" + request.getParameterMap());
		}

		response.setContentType("text/xml");
		this.getTopics(request, response);
		response.getOutputStream().flush();
		response.getOutputStream().close();

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("doGet:ended");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("doPost:started");
			LOGGER.debug("params=" + request.getParameterMap());
		}

		response.setContentType("text/xml");
		this.getTopics(request, response);
		response.getOutputStream().flush();
		response.getOutputStream().close();

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("doPost:ended");

	}

	private void getTopics(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String startRowKey = Constant.TABLE_SELECT_START_ROW.toString();
		String startRowVal = request.getParameter(startRowKey);
		if (StringUtils.isEmpty(startRowVal)
				|| !StringUtils.isNumeric(startRowVal)
				|| Integer.parseInt(startRowVal) < 0) {
			response.getOutputStream()
					.print(ServletResponse.fail(SvInsertDataReference.class
							.getName()));
			if (LOGGER.isEnabledFor(Level.ERROR))
				LOGGER.error("INVALID request parameter value for \""
						+ startRowVal + "\", expected number >= 0, got "
						+ startRowKey + "=[" + startRowVal + "]");

			return;
		}

		String selectCountKey = Constant.TABLE_SELECT_COUNT.toString();
		String selectCountVal = request.getParameter(selectCountKey);
		if (StringUtils.isEmpty(selectCountVal)
				|| !StringUtils.isNumeric(selectCountVal)
				|| Integer.parseInt(selectCountVal) < 0) {
			response.getOutputStream()
					.print(ServletResponse.fail(SvInsertDataReference.class
							.getName()));
			if (LOGGER.isEnabledFor(Level.ERROR))
				LOGGER.error("INVALID request parameter value for \""
						+ selectCountVal + "\", expected number >= 0, got "
						+ selectCountKey + "=[" + selectCountVal + "]");

			return;
		}		
		
		int startRow = Integer.parseInt(request.getParameter("sr"));
		int count = Integer.parseInt(request.getParameter("c"));
		String tableName = request.getParameter("tbl");

		DBUtil dbUtil = DBUtil.getInstance();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			StringWriter sw = new StringWriter();
			XMLOutputFactory xof = XMLOutputFactory.newInstance();
			XMLStreamWriter xtw = null;
			xtw = xof.createXMLStreamWriter(sw);
			// xtw.writeComment("Generated by eGENE engine");
			// xtw.writeStartDocument("utf-8", "1.0");
			xtw.writeStartElement("data");
			xtw.writeStartElement("datum");

			conn = dbUtil.getConnection();
			stmt = conn.createStatement();
			String sql = "select id, value master_topics from " + tableName
					+ " limit " + Integer.toString(startRow) + ", "
					+ Integer.toString(count);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("sql: " + sql);

			rs = stmt.executeQuery(sql);
			while (rs.next()) {

				String id = rs.getString(1);
				String value = rs.getString(2);

				xtw.writeStartElement("id");
				xtw.writeCharacters(id);
				xtw.writeEndElement();

				xtw.writeStartElement("value");
				xtw.writeCharacters(value);
				xtw.writeEndElement();

			}

			xtw.writeEndElement();
			xtw.writeEndElement();
			xtw.writeEndDocument();
			xtw.flush();
			xtw.close();

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("SERVLET-OUTPUT=" + sw.toString());
			response.getOutputStream().print(sw.toString());

		} catch (XMLStreamException e) {
			if (LOGGER.isEnabledFor(Level.ERROR))
				LOGGER.error(e.getMessage(), e);
			throw new EgeneWebException(e.getMessage(), e);
		} catch (DBException e) {
			if (LOGGER.isEnabledFor(Level.ERROR))
				LOGGER.error(e.getMessage(), e);
			throw new EgeneWebException(e.getMessage(), e);
		} catch (SQLException e) {
			if (LOGGER.isEnabledFor(Level.ERROR))
				LOGGER.error(e.getMessage(), e);
			throw new EgeneWebException(e.getMessage(), e);
		} finally {
			dbUtil.closeResultSet(rs);
			dbUtil.closeStatement(stmt);
			dbUtil.closeConnection(conn);
		}
	}

}
