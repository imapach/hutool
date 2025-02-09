package cn.hutool.core.util;

import cn.hutool.core.compress.ZipReader;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Console;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link ZipUtil}单元测试
 * @author Looly
 *
 */
public class ZipUtilTest {

	@Test
	public void appendTest() throws IOException {
		File appendFile = FileUtil.file("test-zip/addFile.txt");
		File zipFile = FileUtil.file("test-zip/test.zip");

		// 用于测试完成后将被测试文件恢复
		File tempZipFile = FileUtil.createTempFile(FileUtil.file("test-zip"));
		tempZipFile.deleteOnExit();
		FileUtil.copy(zipFile, tempZipFile, true);

		// test file add
		List<String> beforeNames = zipEntryNames(tempZipFile);
		ZipUtil.append(tempZipFile.toPath(), appendFile.toPath());
		List<String> afterNames = zipEntryNames(tempZipFile);

		// 确认增加了文件
		Assert.assertEquals(beforeNames.size() + 1, afterNames.size());
		Assert.assertTrue(afterNames.containsAll(beforeNames));
		Assert.assertTrue(afterNames.contains(appendFile.getName()));

		// test dir add
		beforeNames = zipEntryNames(tempZipFile);
		File addDirFile = FileUtil.file("test-zip/test-add");
		ZipUtil.append(tempZipFile.toPath(), addDirFile.toPath());
		afterNames = zipEntryNames(tempZipFile);

		// 确认增加了文件和目录，增加目录和目录下一个文件，故此处+2
		Assert.assertEquals(beforeNames.size() + 2, afterNames.size());
		Assert.assertTrue(afterNames.containsAll(beforeNames));
		Assert.assertTrue(afterNames.contains(appendFile.getName()));

		// rollback
		Assert.assertTrue(String.format("delete temp file %s failed", tempZipFile.getCanonicalPath()), tempZipFile.delete());
	}

	/**
	 * 获取zip文件中所有一级文件/文件夹的name
	 *
	 * @param zipFile 待测试的zip文件
	 * @return zip文件中一级目录下的所有文件/文件夹名
	 */
	private List<String> zipEntryNames(File zipFile) {
		List<String> fileNames = new ArrayList<>();
		ZipReader reader = ZipReader.of(zipFile, CharsetUtil.CHARSET_UTF_8);
		reader.read(zipEntry -> fileNames.add(zipEntry.getName()));
		reader.close();
		return fileNames;
	}

	@Test
	@Ignore
	public void zipDirTest() {
		ZipUtil.zip(new File("d:/test"));
	}

	@Test
	@Ignore
	public void unzipTest() {
		File unzip = ZipUtil.unzip("f:/test/apache-maven-3.6.2.zip", "f:\\test");
		Console.log(unzip);
	}

	@Test
	@Ignore
	public void unzipTest2() {
		File unzip = ZipUtil.unzip("f:/test/各种资源.zip", "f:/test/各种资源", CharsetUtil.CHARSET_GBK);
		Console.log(unzip);
	}

	@Test
	@Ignore
	public void unzipFromStreamTest() {
		File unzip = ZipUtil.unzip(FileUtil.getInputStream("e:/test/hutool-core-5.1.0.jar"), FileUtil.file("e:/test/"), CharsetUtil.CHARSET_UTF_8);
		Console.log(unzip);
	}

	@Test
	@Ignore
	public void unzipChineseTest() {
		ZipUtil.unzip("d:/测试.zip");
	}

	@Test
	@Ignore
	public void unzipFileBytesTest() {
		byte[] fileBytes = ZipUtil.unzipFileBytes(FileUtil.file("e:/02 电力相关设备及服务2-241-.zip"), CharsetUtil.CHARSET_GBK, "images/CE-EP-HY-MH01-ES-0001.jpg");
		Assert.assertNotNull(fileBytes);
	}

	@Test
	public void gzipTest() {
		String data = "我是一个需要压缩的很长很长的字符串";
		byte[] bytes = StrUtil.utf8Bytes(data);
		byte[] gzip = ZipUtil.gzip(bytes);

		//保证gzip长度正常
		Assert.assertEquals(68, gzip.length);

		byte[] unGzip = ZipUtil.unGzip(gzip);
		//保证正常还原
		Assert.assertEquals(data, StrUtil.utf8Str(unGzip));
	}

	@Test
	public void zlibTest() {
		String data = "我是一个需要压缩的很长很长的字符串";
		byte[] bytes = StrUtil.utf8Bytes(data);
		byte[] gzip = ZipUtil.zlib(bytes, 0);

		//保证zlib长度正常
		Assert.assertEquals(62, gzip.length);
		byte[] unGzip = ZipUtil.unZlib(gzip);
		//保证正常还原
		Assert.assertEquals(data, StrUtil.utf8Str(unGzip));

		gzip = ZipUtil.zlib(bytes, 9);
		//保证zlib长度正常
		Assert.assertEquals(56, gzip.length);
		byte[] unGzip2 = ZipUtil.unZlib(gzip);
		//保证正常还原
		Assert.assertEquals(data, StrUtil.utf8Str(unGzip2));
	}

	@Test
	@Ignore
	public void zipStreamTest(){
		//https://github.com/looly/hutool/issues/944
		String dir = "d:/test";
		String zip = "d:/test.zip";
		try (OutputStream out = new FileOutputStream(zip)){
			//实际应用中, out 为 HttpServletResponse.getOutputStream
			ZipUtil.zip(out, Charset.defaultCharset(), false, null, new File(dir));
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	@Test
	@Ignore
	public void zipStreamTest2(){
		// https://github.com/dromara/hutool/issues/944
		String file1 = "d:/test/a.txt";
		String file2 = "d:/test/a.txt";
		String file3 = "d:/test/asn1.key";

		String zip = "d:/test/test2.zip";
		try (OutputStream out = new FileOutputStream(zip)){
			//实际应用中, out 为 HttpServletResponse.getOutputStream
			ZipUtil.zip(out, Charset.defaultCharset(), false, null,
					new File(file1),
					new File(file2),
					new File(file3)
			);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}
}
