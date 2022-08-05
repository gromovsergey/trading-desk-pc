import logging
import SOAPpy
from SOAPpy import Types

logger = logging.getLogger(__name__)


def confluence_soap_parser(xml_str, rules=None, ignore_ext=None,
                           parser=SOAPpy.Parser._parseSOAP):
    attribute = 'xsi:type="soapenc:Array"'
    xml_str = xml_str.replace('%s %s' % (attribute, attribute), attribute)
    return parser(xml_str, rules=rules)

SOAPpy.Parser._parseSOAP = confluence_soap_parser


class Confluence:
    def __init__(self, url, user, passfile):
        self.user = user
        password = self._read_password(passfile)
        self.url = url + "/rpc/soap-axis/confluenceservice-v2?wsdl"
        self.soap = SOAPpy.WSDL.Proxy(self.url)
        self.auth = self.soap.login(self.user, password)

    def _read_password(self, passfile):
        with open(passfile) as h:
            return h.read().strip()

    def getMethods(self):
        return map(lambda x: (
            x[0], x[1].getInParameters(), x[1].getOutParameters()),
            self.soap.methods.items())

    def getPage(self, space, title):
        return self.soap.getPage(self.auth, space, title)

    def getPageById(self, pageid):
        return self.soap.getPage(self.auth, Types.longType(pageid))

    def getChildren(self, pageid):
        return self.soap.getChildren(self.auth, Types.longType(pageid))

    def storePage(self, page):
        return self.soap.storePage(self.auth, page)
