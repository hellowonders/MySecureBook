package com.qdt.blockchain.block;

import java.io.Serializable;

import com.qdt.blockchain.support.utils.HashUtil;

public class Block implements Serializable {
	private static final long serialVersionUID = 7803503309475567495L;
	private String hash;
    private String previousHash;
    private String nextHash;
    private String title;
    private String description;
    private String fileName;
    private byte[] data;
    /**
     * as number of milliseconds since 1/1/1970.
     */
    private long timeStamp;

    /**
     * Block Constructor.
     * @param previousHash
     */
    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

	/**
     * Calculate new hash based on blocks contents
     * @return
     */
    public String calculateHash() {
        return HashUtil.applySha256(previousHash + Long.toString(timeStamp) + data);
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getNextHash() {
		return nextHash;
	}

	public void setNextHash(String nextHash) {
		this.nextHash = nextHash;
	}

	public long getTimeStamp() {
		return timeStamp;
	}
}
