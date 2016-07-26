package com.codemonkey.learn.codehelper
import groovy.sql.Sql

import org.apache.bsf.util.StringUtils;
import org.codehaus.groovy.runtime.InvokerHelper

import groovy.text.*;
class VoUtils extends Script {
	static void main(String[] args) {
		InvokerHelper.runScript(VoUtils, args)
	}
	def config = {
		def config = [:]
		//config.url = "jdbc:oracle:thin:@172.19.0.236:1521:orcl"
		config.url = "jdbc:oracle:thin:@localhost:1521:orcl"
		config.user = 'irbs'
		config.pwd = 'irbs'
		return config
	}

	def run() {
		def c = config()
		def binding = getVo(c.url,c.user,c.pwd)
//		def template = getTemplate('BO.template',binding)
//		writeFile(template,binding.clazzName)
//		
//		def sqlTemplate = getTemplate('Sql.template',binding)
//		println sqlTemplate.toString()
		
	}

	def writeFile = {t,n ->
		File f = new File("C:/Users/hjy/Desktop/${n}.java")
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "utf-8"));
		writer.write(t.toString());
		writer.close();
	}
	
	def getTemplate = {fileName,binding ->
		def t = new File("D:/tansun/work/workspace/groovy/src/com/codemonkey/learn/codehelper/${fileName}")
		def template = new GStringTemplateEngine().createTemplate(t).make(binding)
		return template
	}


	def getVo = {url,u,p->
		def sql = Sql.newInstance(url, u, p, 'oracle.jdbc.driver.OracleDriver')

		def sqlStr = '''
			select t.table_name  tableName,
			       tc.comments   tableComments,
			       t.column_name columnName,
			       t.DATA_TYPE   dataType,
			       c.comments    comments
			  from user_tab_columns t, user_col_comments c , user_tab_comments tc
			 where t.TABLE_NAME = c.table_name
			   and t.COLUMN_NAME = c.column_name
			   and tc.TABLE_NAME = t.TABLE_NAME
			  order by t.COLUMN_ID
		'''


		def first = sql.firstRow(sqlStr);
		def cols =  first.keySet();
		print cols
	}
}
