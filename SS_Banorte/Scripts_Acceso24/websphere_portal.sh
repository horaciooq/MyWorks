
XMLACCESS=$1
URLCONFIG=$2
WPUSER=$3
WPPASS=$4
WEBAPP=$5
PORTLETAPP=$6
WAR=$7

SCRIPT_DIR=`dirname $0`

#echo "$SCRIPT_DIR/$PORTLETAPP.xml"
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
 <request xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
    xsi:noNamespaceSchemaLocation=\"PortalConfig_8.0.0.xsd\" 
    type=\"update\" create-oids=\"true\"> 
    <portal action=\"locate\"> 
      <web-app action=\"update\" active=\"true\" uid=\"$WEBAPP\"> 
          <url>file:$WAR</url> 
          <!-- The uid must match uid attribute of concrete-portlet-app in portlet.xml. --> 
          <!-- The name attribute must match content of portlet-name subtag  of concrete-portlet in portlet.xml. --> 
          <portlet-app action=\"update\" active=\"true\" uid=\"$PORTLETAPP\"> 
          </portlet-app> 
        </web-app> 
     </portal> 
 </request>" > "$SCRIPT_DIR/$PORTLETAPP.xml"


#echo $XMLACCESS -url $URLCONFIG -in "$SCRIPT_DIR/$PORTLETAPP.xml" -user $WPUSER -password $WPPASS
$XMLACCESS -url $URLCONFIG -in "$SCRIPT_DIR/$PORTLETAPP.xml" -user $WPUSER -password $WPPASS