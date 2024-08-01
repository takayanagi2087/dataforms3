package jp.dataforms.fw.report;

import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.render.print.PrintRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.dao.file.ImageData;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.common.ImageField;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.MapUtil;


/**
 * XSL-FOレポート。
 *
 */
public class XslFoReport extends Report {

	/**
	 * Logger.
	 */
	private Logger logger = LogManager.getLogger(XslFoReport.class);

	/**
	 * テンプレートファイルのパス。
	 */
	private String templatePath = null;

	/**
	 * コンストラクタ。
	 */
	public XslFoReport() {

	}

	/**
	 * コンストラクタ。
	 * @param templatePath テンプレートパス。
	 */
	public XslFoReport(final String templatePath) {
		this.templatePath = templatePath;
	}

	/**
	 * テンプレートパスを取得します。
	 * @return テンプレートパス。
	 */
	public String getTemplatePath() {
		return templatePath;
	}

	/**
	 * テンプレートパスを設定します。
	 * @param templatePath テンプレートパス。
	 */
	public void setTemplatePath(final String templatePath) {
		this.templatePath = templatePath;
	}

	/**
	 * ページ編集バッファ。
	 */
	private String pageBuffer = null;

	/**
	 * ページリスト。
	 */
	private List<String> pageList = null;

	/**
	 * 指定されたページの未設定フィールドをクリアします。
	 * @param page ページインデックス。
	 * @throws Exception 例外。
	 */
	protected void clearField(final int page) throws Exception {
		for (Field<?> field: this.getFieldList()) {
			this.clearField(page, field);
		}
		for (ReportTable tbl: this.getTableList()) {
			String tblid = tbl.getId();
			for (int i = 0;; i++) {
				if (this.pageBuffer.indexOf("${" + tblid + "[") < 0) {
					break;
				}
				for (Field<?> f: tbl.getFieldList()) {
					String id = f.getId();
					String fid = tblid + "[" + i + "]." + id;
					Field<?> field = f.clone();
					field.setId(fid);
					this.clearField(page, field);
				}
			}

		}
	}


	@Override
	protected void printPage(final int page, final Map<String, Object> data) throws Exception {
		this.pageBuffer = (new StringBuilder(this.pageBlockFo)).toString();
		super.printPage(page, data);
		this.clearField(page);
		this.pageBuffer = this.pageBuffer.replaceAll("\\<fo:external-graphic(.*?)src=\"\"(.*?)/\\>", "");
		this.pageList.add(this.pageBuffer + "\n");
	}


	/**
	 * 画像をタグに変換します。
	 * @param data 画像データ。
	 * @return 画像タグ。
	 */
	private String getImageTag(final ImageData data) throws Exception {
		if (data != null) {
			String encoded = Base64.getEncoder().encodeToString(data.readContents());
			String ret = "data:" + data.getContentType() + ";base64, " + encoded;
			return ret;
		} else {
			return "";
		}
	}


	@Override
	protected void printField(final int page, final Field<?> field, final Map<String, Object> data) throws Exception {
		Object cv = "";
		Object obj = MapUtil.getValue(field.getId(), data);
		if (obj != null) {
			if (field instanceof ImageField) {
				ImageData img = (ImageData) obj;
				cv = this.getImageTag(img);
			} else {
				field.setValueObject(obj);
				cv = field.getClientValue();
/*				if (cv instanceof String) {
					cv = StringEscapeUtils.unescapeHtml4((String) cv);
				}*/
			}
		}
		logger.debug("id={}, value={}", field.getId(), cv.toString());
		this.pageBuffer = this.pageBuffer.replace("${" + field.getId() + "}", cv.toString());
	}

	/**
	 * フィールドをクリアします。
	 * @param page ページインデックス。
	 * @param field クリアするフィールド。
	 */
	protected void clearField(final int page, final Field<?> field) {
		this.pageBuffer = this.pageBuffer.replace("${" + field.getId() + "}", "");
	}


	/**
	 * SVGテキストからDocumentに変換。
	 * @param path foファイルのパス。
	 * @return Document。
	 * @throws Exception 例外。
	 */
	private Document getXmlDocument(final String path) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(path));
		return doc;
	}



	/**
	 * NodeをXMLテキストに変換します。
	 * @param node ノード。
	 * @return XMLテキスト。
	 * @throws TransformerException 例外。
	 */
	public String convertToString(final Node node) throws TransformerException{
		DOMSource source = new DOMSource(node);
		StringWriter swriter = new StringWriter();
		StreamResult result = new StreamResult(swriter);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.transform(source, result);
		String nodeText = swriter.toString();
		return nodeText.replaceAll("\\<\\?xml.*\\?\\>", "");
	}

	/**
	 * ネームスペースコンテキスト。
	 *
	 */
	public class NamespaceResolver implements NamespaceContext {
		/**
		 * XMLドキュメント。
		 */
		private Document sourceDocument;

		/**
		 * コンストラクタ。
		 * @param document ドキュメント。
		 */
		public NamespaceResolver(final Document document) {
			sourceDocument = document;
		}

		@Override
		public String getNamespaceURI(final String prefix) {
			if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
				return sourceDocument.lookupNamespaceURI(null);
			} else {
				return sourceDocument.lookupNamespaceURI(prefix);
			}
		}

		@Override
		public String getPrefix(final String namespaceURI) {
			return sourceDocument.lookupPrefix(namespaceURI);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Iterator getPrefixes(final String namespaceURI) {
			return (Iterator) null;
		}
	}

	/**
	 * ドキュメントフレーム。
	 */
	private Document docFo = null;
	/**
	 * PageのFloノード。
	 */
	private Node pageFlow = null;
	/**
	 * 1ページ分のXML。
	 */
	private String pageBlockFo = null;

	@Override
	protected void initPage(final int pages) throws Exception {
		logger.debug(() -> "pages=" + pages);
		this.docFo = this.getXmlDocument(this.templatePath);
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NamespaceResolver(this.docFo));
		Node page = (Node) xpath.evaluate("/fo:root/fo:page-sequence/fo:flow/fo:block", this.docFo, XPathConstants.NODE);
		logger.debug(() -> "page nodeName=" + page.getNodeName());
		this.pageBlockFo = this.convertToString(page).replace("xmlns:fo=\"http://www.w3.org/1999/XSL/Format\"", "page-break-before=\"always\"");
		this.pageFlow = (Node) xpath.evaluate("/fo:root/fo:page-sequence/fo:flow", this.docFo, XPathConstants.NODE);
		this.pageFlow.removeChild(page);
		Node textNode = this.docFo.createTextNode("${pageBlock}\n\n");
		this.pageFlow.appendChild(textNode);
		this.pageList = new ArrayList<String>();
	}


	/**
	 * FopFactoryを取得します。
	 * @return FopFactory。
	 * @throws Exception 例外。
	 */
	private FopFactory newFopFactory() throws Exception {
		String conf = WebComponent.getServlet().getServletContext().getRealPath(DataFormsServlet.getApacheFopConfig());
		FopFactory fopFactory = FopFactory.newInstance(new File(conf));
		return fopFactory;
	}

	/**
	 * FOを指定して、PDFを取得します。
	 * @param foXml Fo形式の文字列。
	 * @return PDFファイル。
	 * @throws Exception 例外。
	 */
	private byte[] createPdf(final String foXml) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(foXml.getBytes(DataFormsServlet.getEncoding()));
			try {
				Source fo = new StreamSource(is);
				//FOPをセットアップする
				FopFactory fopFactory = this.newFopFactory();
				Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, os);
				//XSL FO⇒PDFに変換する
				Result result = new SAXResult(fop.getDefaultHandler());
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory.newTransformer();
				transformer.transform(fo, result);
			} finally {
				is.close();
			}
		} finally {
			os.close();
		}
		return os.toByteArray();
	}

	/**
	 * データが設定されたXSL-FO文字列を取得します。
	 * @return データが設定されたXSL-FO文字列。
	 * @throws TransformerException 例外。
	 */
	protected String getXslFo() throws TransformerException {
		String xml = this.convertToString(this.docFo);
		StringBuilder sb = new StringBuilder();
		for (String p: this.pageList) {
			sb.append(p);
		}
		xml = xml.replace("${pageBlock}", sb.toString());
		return xml;
	}

	@Override
	public byte[] getReport() throws Exception {
		String xml = this.getXslFo();
		logger.debug(() -> "xslFo=" + xml);
		return this.createPdf(xml);
	}


	/**
	 * FOを指定したプリンターに印刷します。。
	 * @param foXml Fo形式の文字列。
	 * @param printJob 印刷ジョブ。
	 * @throws Exception 例外。
	 */
	@SuppressWarnings("unchecked")
	private void print(final String foXml, final PrinterJob printJob) throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(foXml.getBytes(DataFormsServlet.getEncoding()));
		try {
			FopFactory fopFactory = this.newFopFactory();
			Source src = new StreamSource(is);
			//FOPをセットアップする
			FOUserAgent userAgent = fopFactory.newFOUserAgent();
			userAgent.getRendererOptions().put(PrintRenderer.PRINTER_JOB, printJob);
			PrintRenderer renderer = new PrintRenderer(userAgent);
			userAgent.setRendererOverride(renderer);
			Fop fop = fopFactory.newFop(userAgent);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(); // identity transformer
			Result res = new SAXResult(fop.getDefaultHandler());
			transformer.transform(src, res);
		} finally {
			is.close();
		}
	}

	/**
	 * プリンターに直接印刷します。
	 * @param data 印刷するデータ。
	 * @param printJob 印刷JOB。
	 * @throws Exception 例外。
	 */
	public void print(final Map<String, Object> data, final PrinterJob printJob) throws Exception {
		this.buildReport(data);
//		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + this.getXslFo();
		String xml = this.getXslFo();
		logger.debug(() -> "xslFo=" + xml);
		this.print(xml, printJob);
	}

}
