package com.codemonkey.learn.xml

import groovy.util.XmlParser
import groovy.util.XmlSlurper
import groovy.xml.*;

def xmlFile = new XmlParser().parse("C:/Users/7/Desktop/decisiomTreeSub.xml");
RuleCollections.modelItemList.decisionId
def writer = new StringWriter()
def xml = new MarkupBuilder(writer) 

def xmlString = "<movie>the godfather</movie>" 

def xmlWriter = new StringWriter() 
def xmlMarkup = new MarkupBuilder(xmlWriter)


xmlMarkup
    .'x:movies'('xmlns:x':'http://www.groovy-lang.org') {
        (1..3).each { n -> 
            'x:movie'(id: n, "the godfather $n")
            if (n % 2 == 0) { 
                'x:movie'(id: n, "the godfather $n (Extended)")
            }
        }
    }

print xmlWriter.toString()