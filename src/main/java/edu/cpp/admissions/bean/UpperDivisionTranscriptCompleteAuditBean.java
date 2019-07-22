package edu.cpp.admissions.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="upper_div_transcript_complete_report")
public class UpperDivisionTranscriptCompleteAuditBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2297493482213763946L;

	@Id
	@Column(name="sr")
	@GeneratedValue(strategy=GenerationType.IDENTITY, generator = "SR_SEQ")
	@SequenceGenerator(name = "SR_SEQ", sequenceName = "SR_SEQ", allocationSize = 1)
	private Long sr;
	
	@Column(name="bronco_id")
	private Long broncoId;

	@Temporal(TemporalType.DATE)
	@Column(name="date_start", nullable = false, updatable = false)
	private Date dateStart;

	@Temporal(TemporalType.DATE)
	@Column(name="final_date", nullable = true, updatable = false)
	private Date finalDate;
	
	@Column(name="status")
	private String status;
	
	@Column(name="processor")
	private String processor;
	
	@Column(name="wam")
	private String wam;

	/**
	 * @return the sr
	 */
	public Long getSr() {
		return sr;
	}

	/**
	 * @param sr the sr to set
	 */
	public void setSr(Long sr) {
		this.sr = sr;
	}

	/**
	 * @return the broncoId
	 */
	public Long getBroncoId() {
		return broncoId;
	}

	/**
	 * @param broncoId the broncoId to set
	 */
	public void setBroncoId(Long broncoId) {
		this.broncoId = broncoId;
	}

	/**
	 * @return the dateStart
	 */
	public Date getDateStart() {
		return dateStart;
	}

	/**
	 * @param dateStart the dateStart to set
	 */
	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	/**
	 * @return the finalDate
	 */
	public Date getFinalDate() {
		return finalDate;
	}

	/**
	 * @param finalDate the finalDate to set
	 */
	public void setFinalDate(Date finalDate) {
		this.finalDate = finalDate;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the processor
	 */
	public String getProcessor() {
		return processor;
	}

	/**
	 * @param processor the processor to set
	 */
	public void setProcessor(String processor) {
		this.processor = processor;
	}

	/**
	 * @return the wam
	 */
	public String getWam() {
		return wam;
	}

	/**
	 * @param wam the wam to set
	 */
	public void setWam(String wam) {
		this.wam = wam;
	}
}
