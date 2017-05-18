package xmu.lgp.lly.integration;

import com.alibaba.dubbo.common.Constants;

public class Tester {
	
	public static void main(String[] args) {
		String rst[] = Constants.COMMA_SPLIT_PATTERN.split("a,,b,,,c");
	}
}
