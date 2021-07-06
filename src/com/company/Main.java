package com.company;

public class Main<sequence> {

    public static class IdWorker {
        //因为二进制里面第一个bit位如果是1，代表是负数，因为我们的ID是自增的
        // 机器ID 二进制5位，32位减掉1位剩余31位。
        private long workerId;
        //机房Id 进制5位，32位减掉1位剩余31位。
        private long datacenterId;
        //每一毫秒产生多个Id序列号
        private long sequence;
        //设置初始时间
        private long twepoch = 1586442;
        //设置5位机房Id
        private long workerIdbits = 5L;
        //设置5位的机器Id
        private long datacenterIdBits = 5L;
        //设置每毫秒内产生的Id
        private long sequenceBits = 12L;
        //设置二进制的运算
        private long maxWorkerId = -1L ^ (-1L << workerIdbits);  //^按位异或
        //就就是5bit位最多只能有31个数字机房Id最多智能有32位以内
        private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
        //赋值
        private long workerIdShift = sequenceBits;
        private long datacenterIdShift = sequenceBits + workerIdbits;
        private long timestampLeftShift = sequenceBits + workerIdbits + datacenterIdBits;
        private long sequenceMask = -1L ^ (-1L << sequenceBits);
        //记录一下产生时间的毫秒数，判断是否同一秒内
        private long lastTimestamp = -1L;

        public IdWorker(int i, int i1, int i2) {
        }

        public long getWorkerId() {
            return workerId;
        }

        public long getDatacenterId() {
            return datacenterId;
        }

        public long getTimestamp() {
            return System.currentTimeMillis();
        }

        public void IWorker(long workId, long datacenterIdlong, long sequence) {
            //检查机房Id和机器Id是否超过31,然后不能小于0
            if (workId > maxWorkerId || workId < 0) {
                throw new IllegalArgumentException(
                        String.format("WWWW", maxWorkerId)
                );
            }
            if (datacenterId > maxDatacenterId || datacenterId < 0) {
                throw new IllegalArgumentException(
                        String.format("WWWW", maxDatacenterId)
                );
            }
            this.workerId = workerId;
            this.workerId = workerId;
            this.workerId = workerId;
        }

        // 这个是核心方法，通过调用nextId()方法，让当前这台机器上的snowflake算法程序生成一个全局唯一的id
        public synchronized long nextId() {
            //这就是获取当前时间戳，单位毫秒
            long timestamp = timeGen();
            if (timestamp < lastTimestamp) {
                System.out.printf("clock is moving backwards. Rejecting requests until %d.", lastTimestamp);
                throw new RuntimeException(
                        String.format("Clock moved backwards. Refusing to generate id for %d mi lliseconds",
                                lastTimestamp - timestamp));

            }
            //下面是说假设在同一个毫秒内，又发送了一个请求生成一个ID
            //这时候就得把sequence序号传递给递增1，最多就是4096
            if (lastTimestamp == timestamp) {
                //这个意思是说一个毫秒内最多只能有4096个数字，无论你传递多少进来
                //这个位运算保证始终就是在4096这个范围内，避免你自己传递个sequence超过了4096这个范围
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0;
            }
            // 这儿记录一下最近一次生成id的时间戳，单位是毫秒
            lastTimestamp = timestamp;
            // 这儿就是最核心的二进制位运算操作，生成一个64bit的id
            // 先将当前时间戳左移，放到41 bit那儿；将机房id左移放到5 bit那儿；将机器id左 移放到5 bit那儿；将序号放最后12 bit
            // 最后拼接起来成一个64 bit的二进制数字，转换成10进制就是个long型
            return ((timestamp - twepoch) << timestampLeftShift) |
                    (datacenterId << datacenterIdShift) |
                    (workerId << workerIdShift) |
                    sequence;
        }

        /**
         * 当某一毫秒的时间，产生的id数 超过4095，系统会进入等待，直到下一毫秒，系统继 续产生ID
         *
         * @param lastTimestamp
         * @return
         */
        private long tilNextMillis(long lastTimestamp) {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        //获取当前时间戳
        private long timeGen() {
            return System.currentTimeMillis();
        }

        /**
         * main 测试类
         *
         * @param args
         */
        public static void main(String[] args) {
            System.out.println(1 & 4596);
            System.out.println(3 & 4596);
            System.out.println(3 & 4596);
            System.out.println(2 & 4596);
            System.out.println(6 & 4596);
            System.out.println(6 & 4596);
            IdWorker worker = new IdWorker(2,1,1);
            for (int i = 0; i < 20; i++) {
                System.out.println(worker.nextId());
            }
        }
    }
}

