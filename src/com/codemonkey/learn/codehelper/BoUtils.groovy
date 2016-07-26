package com.codemonkey.learn.codehelper
import groovy.sql.Sql

import org.apache.bsf.util.StringUtils;
import org.codehaus.groovy.runtime.InvokerHelper

import groovy.text.*;
class BoUtils extends Script {
	static void main(String[] args) {
		InvokerHelper.runScript(BoUtils, args)
	}
	def config = {
		def config = [:]
		//params.url = "jdbc:oracle:thin:@localhost:1521:orcl"
		config.user = 'irbs'
		config.pwd = 'irbs'
		config.tableName = 'IRB_CUST_GROUP_SUB_RELA'
		return config
	}

	def run() {
		def c = config()
		def binding = getTableInfo(c.url,c.user,c.pwd,c.tableName)
		def template = getTemplate('BO.template',binding)
		writeFile(template,binding.clazzName)
		
		def sqlTemplate = getTemplate('Sql.template',binding)
		println sqlTemplate.toString()
		
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


	def getTableInfo = {url,u,p,n->
		def params = [n]
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
			  and t.TABLE_NAME = ?
			  order by t.COLUMN_ID
		'''

		def toJavaStyle = {str ->
			str = str.toLowerCase()
			str.replaceAll(/_[a-z]/, {
				if(it.size()>= 2){
					it[1].toUpperCase()
				}
			})
		}

		def toJaveType = { dataType ->
			def t = [:]
			switch(dataType){
				case 'VARCHAR2':
						t.type='String'; break
				case 'DATE' :
							t.type = 'Date'; t.imp = 'java.sql.Date';break
				case 'NUMBER':
							t.type = 'BigDecimal'; t.imp = 'java.math.BigDecimal'; break
				case 'CHAR':
						t.type = 'String'; break
				default :
					t.type = 'Î´ÖªÀàÐÍ'
			}
			return t
		}

		def toFirstUpperCase = {c ->
			c.replaceFirst(/[a-z]/, {
				it[0].toUpperCase()
			})
		}

		def map = [:] //[cRows : rows , jTableName,sTableName]
		def cRows = [];
		sql.eachRow(sqlStr,params) { row ->
			def cRow = [:];
			cRow.with {
				jColumnName = toJavaStyle(row.columnName)
				columnName = row.columnName
				jDataType = toJaveType(row.dataType).type
				jImport = toJaveType(row.dataType).imp
				dataType = row.dataType
				comments = row.comments
			}
			cRows << cRow
			map.jTableName = toJavaStyle(row.tableName);
			map.sTableName = row.tableName
			map.tableComments = row.tableComments
			map.clazzName = toFirstUpperCase(map.jTableName)
		}
		map.cRows = cRows
		sql.close()
		print map
		return map
	}
}
